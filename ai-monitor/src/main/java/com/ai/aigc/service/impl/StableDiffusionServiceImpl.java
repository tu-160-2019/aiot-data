package com.ai.aigc.service.impl;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import com.ai.aigc.service.StableDiffusionService;
import com.ai.config.AiModelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ai.aigc.model.StableDiffusion;
import com.ai.aigc.model.Img2ImgStableDiffusion;

@Service
public class StableDiffusionServiceImpl implements StableDiffusionService {
    @Autowired
    AiModelConfig aiConfig;

    StableDiffusion stableDiffusion;

    Img2ImgStableDiffusion img2img;

    private static final String prompt = "a photo of an astronaut riding a horse on mars";
    private static final String negative_prompt = "";

    public String text2img(String text) {
        try {
            // 默认使用cpu推理，也可以换成gpu
//            stableDiffusion = getGpuInstance();
            stableDiffusion = getCpuInstance();
            NDArray latent = stableDiffusion.inference("a photo of an astronaut riding a horse on mars", "");

            stableDiffusion.saveImage(latent, "out_pt_cpu.png", aiConfig.getPath() + "output/");
            return aiConfig.getDomain() + "/upload/output/out_pt_cpu.png";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String img2img(String filePath, String text) {
        try {
            img2img = getImg2ImgInstance();
            // "A fantasy landscape, trending on artstation"
            NDArray latent = img2img.inference(text, "", aiConfig.getPath() + "/" + filePath);

            img2img.saveImage(latent, "out_pt_gpu.png", aiConfig.getPath() + "output/");
            return aiConfig.getDomain() + "/upload/output/out_pt_gpu.png";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private StableDiffusion getCpuInstance() {
        if(stableDiffusion == null) {
            try{
                stableDiffusion = new StableDiffusion("pytorch_cpu", Device.cpu(), 512, 512);
                stableDiffusion.setTextEncoderModelName("text_encoder");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stableDiffusion;
    }

    private StableDiffusion getGpuInstance() {
        if(stableDiffusion == null) {
            try{
                stableDiffusion = new StableDiffusion("pytorch_gpu", Device.gpu(), 512, 512);
                stableDiffusion.setTextEncoderModelName("text_encoder_model_gpu0");
                stableDiffusion.setNetPredictorModelName("unet_traced_model_gpu0");
                stableDiffusion.setDecoderModelName("vae_decode_model_gpu0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stableDiffusion;
    }

    private Img2ImgStableDiffusion getImg2ImgInstance() {
        if(img2img == null) {
            try{
                img2img = new Img2ImgStableDiffusion();
                img2img.setEngineName("PyTorch");
                img2img.setSdArtifacts("pytorch_gpu");
                img2img.setTextEncoderModelName("text_encoder_model_gpu0");
                img2img.setDevice(Device.gpu());
                img2img.setNetPredictorModelName("unet_traced_model_gpu0");
                img2img.setDecoderModelName("vae_decode_model_gpu0");
                img2img.setVocabDictionary("/vocab_dictionary.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return img2img;
    }
}
