package com.ai.aigc.model;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.engine.Engine;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class StableDiffusion {
    private String sdArtifacts = "pytorch_cpu";
    private String vocabDictionary = "/vocab_dictionary.json";
    private String tokenizerName = "openai/clip-vit-large-patch14";
    private Device device = Device.cpu();
    private int height = 512;
    private int width = 512;

    private String prompt = "a photo of an astronaut riding a horse on mars";
    private String negative_prompt = "";

    private int UNKNOWN_TOKEN = 49407;
    private int MAX_LENGTH = 77;
    private int steps = 25;
    private int offset = 1;
    private float guidanceScale = (float) 7.5;
    private Engine engine;
    private String engineName = "PyTorch";
    private NDManager manager;
    private HuggingFaceTokenizer tokenizer = null;
    private String textEncoderModelName = "text_encoder";
    private String uncondEncodingModelName;
    private String netPredictorModelName = "unet_traced_model";
    private String decoderModelName = "vae_decode_model";

    public StableDiffusion(){
        initTokenizer();
    }

    public StableDiffusion(String sdArtifacts, Device device){
        this.sdArtifacts = sdArtifacts;
        this.device = device;
        initTokenizer();
    }

    public StableDiffusion(String sdArtifacts, Device device, int height, int width) {
        this.sdArtifacts = sdArtifacts;
        this.device = device;
        this.height = height;
        this.width = width;

        initTokenizer();
    }

    private void initTokenizer() {
        try {
            tokenizer =
                    HuggingFaceTokenizer.builder()
                            .optManager(manager)
                            .optPadding(true)
                            .optPadToMaxLength()
                            .optMaxLength(MAX_LENGTH)
                            .optTruncation(true)
                            .optTokenizerName(tokenizerName)
                            .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NDArray inference() {
        try {
            engine = Engine.getEngine(engineName); // PyTorch OnnxRuntime
            manager = NDManager.newBaseManager(device, engine.getEngineName());

            NDList textEncoding = SDTextEncoder(SDTextTokenizer(prompt));
            NDList uncondEncoding = SDTextEncoder(SDTextTokenizer(negative_prompt));
            System.out.println(Arrays.toString(textEncoding.get(1).toFloatArray()));

            NDArray textEncodingArray = textEncoding.get(1);
            NDArray uncondEncodingArray = uncondEncoding.get(1);

            NDArray embeddings = uncondEncodingArray.concat(textEncodingArray);

            Shape shape = new Shape(1, 4, height / 8, width / 8);
            NDArray latent = manager.randomNormal(shape);

            StableDiffusionPNDMScheduler scheduler = new StableDiffusionPNDMScheduler(manager);
            scheduler.setTimesteps(steps, offset);

            Predictor<NDList, NDList> predictor = SDUNetPredictor();

            for (int i = 0; i < (int) scheduler.timesteps.size(); i++) {
                NDArray t = manager.create(scheduler.timesteps.toArray()[i]);
                // 如果让分类器自动引导那么latents就会扩大
                NDArray latentModelInput = latent.concat(latent);

                NDArray noisePred = predictor.predict(buildUnetInput(embeddings, t, latentModelInput)).get(0);

                NDList splitNoisePred = noisePred.split(2);
                NDArray noisePredUncond = splitNoisePred.get(0);
                NDArray noisePredText = splitNoisePred.get(1);

                NDArray scaledNoisePredUncond = noisePredText.add(noisePredUncond.neg());
                scaledNoisePredUncond = scaledNoisePredUncond.mul(guidanceScale);
                noisePred = noisePredUncond.add(scaledNoisePredUncond);

                latent = scheduler.step(noisePred, t, latent);
            }

            return latent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Stable diffusion image generated from prompt: \"{}\".", prompt);
        return null;
    }

    private static NDList buildUnetInput(NDArray input, NDArray timestep, NDArray latents) {
        input.setName("encoder_hidden_states");
        NDList list = new NDList();
        list.add(latents);
        list.add(timestep);
        list.add(input);
        return list;
    }

    private NDList SDTextEncoder(NDList input)
            throws ModelNotFoundException, MalformedModelException, IOException,
            TranslateException {
        Criteria<NDList, NDList> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optModelUrls(sdArtifacts)
                        .optModelName(textEncoderModelName)
                        .optEngine(engine.getEngineName())
//                        .optOption("mapLocation", "true")
                        .optDevice(device)
                        .optProgress(new ProgressBar())
                        .optTranslator(new NoopTranslator())
                        .build();

        ZooModel<NDList, NDList> model = criteria.loadModel();
        Predictor<NDList, NDList> predictor = model.newPredictor();
        NDList output = predictor.predict(input);
        model.close();
        return output;
    }

    private Predictor<NDList, NDList> SDUNetPredictor()
            throws ModelNotFoundException, MalformedModelException, IOException {
        Criteria<NDList, NDList> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optModelUrls(sdArtifacts)
                        .optModelName(netPredictorModelName)
                        .optEngine(engine.getEngineName())
//                        .optOption("mapLocation", "true")
                        .optDevice(device)
                        .optProgress(new ProgressBar())
                        .optTranslator(new NoopTranslator())
                        .build();

        ZooModel<NDList, NDList> model = criteria.loadModel();
        return model.newPredictor();
    }

    private NDList SDDecoder(NDList input)
            throws ModelNotFoundException, MalformedModelException, IOException, TranslateException {
        Criteria<NDList, NDList> criteria =
                Criteria.builder()
                        .setTypes(NDList.class, NDList.class)
                        .optModelUrls(sdArtifacts)
                        .optModelName(decoderModelName)
                        .optEngine(engine.getEngineName())
//                        .optOption("mapLocation", "true")
                        .optDevice(device)
                        .optTranslator(new NoopTranslator())
                        .optProgress(new ProgressBar())
                        .build();

        ZooModel<NDList, NDList> model = criteria.loadModel();
        Predictor<NDList, NDList> predictor = model.newPredictor();
        NDList output = predictor.predict(input);
        predictor.close();
        return output;
    }

    private NDList SDTextTokenizer(String prompt) {
        List<String> tokens = tokenizer.tokenize(prompt);
        int[][] tokenValues = new int[1][MAX_LENGTH];
        ObjectMapper mapper = new ObjectMapper();
        File fileObj = new File(sdArtifacts + vocabDictionary); // full_vocab.json vocab_dictionary.json
        try {
            Map<String, Integer> mapObj =
                    mapper.readValue(fileObj, new TypeReference<Map<String, Integer>>() {
                    });
            int counter = 0;
            for (String token : tokens) {
                if (mapObj.get(token) != null) {
                    tokenValues[0][counter] = mapObj.get(token);
                } else {
                    tokenValues[0][counter] = UNKNOWN_TOKEN;
                }
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        NDArray ndArray = manager.create(tokenValues);
        return new NDList(ndArray);
    }

    public void saveImage(NDArray input) throws TranslateException, ModelNotFoundException,
            MalformedModelException, IOException {
        input = input.div(0.18215);

        NDList encoded = new NDList();
        encoded.add(input);
        NDList decoded = SDDecoder(encoded);
        NDArray scaled = decoded.get(0).div(2).add(0.5).clip(0, 1);

        scaled = scaled.transpose(0, 2, 3, 1);
        scaled = scaled.mul(255).round().toType(DataType.INT8, true).get(0);
        Image image = BufferedImageFactory.getInstance().fromNDArray(scaled);

        saveImage(image, "out_pt_cpu", "output/");
    }

    public static void saveImage(Image image, String name, String path) throws IOException {
        Path outputPath = Paths.get(path);
        Files.createDirectories(outputPath);
        Path imagePath = outputPath.resolve(name + ".png");
        image.save(Files.newOutputStream(imagePath), "png");
    }
}

