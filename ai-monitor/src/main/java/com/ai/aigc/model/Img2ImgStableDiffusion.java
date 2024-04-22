package com.ai.aigc.model;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Data
@Slf4j
public class Img2ImgStableDiffusion extends StableDiffusion {

    private float strength = 0.75f;
    private String encoderModelName = "vae_encoder_model_gpu0";

    public Img2ImgStableDiffusion() {
//        this.setPrompt("A fantasy landscape, trending on artstation");
//        this.setNegative_prompt("");

        initTokenizer();
    }

    public NDArray inference(String prompt, String negative_prompt, String filePath) {
        try {
            Path imageFile = Paths.get(filePath);
            Image image = ImageFactory.getInstance().fromFile(imageFile);

            NDList textEncoding = SDTextEncoder(SDTextTokenizer(prompt));
            NDList uncondEncoding = SDTextEncoder(SDTextTokenizer(negative_prompt));

            NDArray textEncodingArray = textEncoding.get(1);
            NDArray uncondEncodingArray = uncondEncoding.get(1);

            NDArray embeddings = uncondEncodingArray.concat(textEncodingArray);

            StableDiffusionPNDMScheduler scheduler = new StableDiffusionPNDMScheduler(getManager());
            scheduler.setTimesteps(getSteps(), getOffset());
            int initTimestep = (int) (getSteps() * strength) + getOffset();
            initTimestep = Math.min(initTimestep, getSteps());
            int timesteps = scheduler.timesteps.get(new NDIndex("-" + initTimestep)).toIntArray()[0];


            NDArray latent = SDEncoder(image);
            NDArray noise = getManager().randomNormal(latent.getShape());
            latent = scheduler.addNoise(latent, noise, timesteps);

            int tStart = Math.max(getSteps() - initTimestep + getOffset(), 0);
            int[] timestepArr = scheduler.timesteps.get(new NDIndex(tStart + ":")).toIntArray();

            Predictor<NDList, NDList> predictor = SDUNetPredictor();

            for (int i = 0; i < timestepArr.length; i++) {
                NDArray t = getManager().create(timestepArr[i]);
                NDArray latentModelInput = latent.concat(latent);
                // embeddings 2,77,768
                // t tensor 981
                // latentModelOutput 2,4,64,64

                NDArray noisePred = predictor.predict(buildUnetInput(embeddings, t, latentModelInput)).get(0);

                NDList splitNoisePred = noisePred.split(2);
                NDArray noisePredUncond = splitNoisePred.get(0);
                NDArray noisePredText = splitNoisePred.get(1);

                NDArray scaledNoisePredUncond = noisePredText.add(noisePredUncond.neg());
                scaledNoisePredUncond = scaledNoisePredUncond.mul(getGuidanceScale());
                noisePred = noisePredUncond.add(scaledNoisePredUncond);

                latent = scheduler.step(noisePred, t, latent);
            }

            log.info("Stable diffusion image generated from prompt: \"{}\".", prompt);

            return latent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private NDArray SDEncoder(Image input)
            throws ModelNotFoundException, MalformedModelException, IOException, TranslateException {
        Criteria<Image, NDArray> criteria =
                Criteria.builder()
                        .setTypes(Image.class, NDArray.class)
                        .optModelUrls(getSdArtifacts())
                        .optModelName(encoderModelName)
                        .optEngine(getEngine().getEngineName())
//                        .optOption("mapLocation", "true")
                        .optDevice(getDevice())
                        .optTranslator(new EncoderTranslator())
                        .optProgress(new ProgressBar())
                        .build();

        ZooModel<Image, NDArray> model = criteria.loadModel();
        Predictor<Image, NDArray> predictor = model.newPredictor();
        NDArray output = predictor.predict(input);
        model.close();
        return output;
    }

}
