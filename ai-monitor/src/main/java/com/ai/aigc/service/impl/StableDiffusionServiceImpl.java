package com.ai.aigc.service.impl;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import org.springframework.stereotype.Service;

import com.ai.aigc.model.StableDiffusion;

@Service
public class StableDiffusionServiceImpl {
    StableDiffusion stableDiffusion;

    private static final String prompt = "a photo of an astronaut riding a horse on mars";
    private static final String negative_prompt = "";

    public void cpu() {
        try {
            // 默认使用cpu推理，也可以换成gpu
//            stableDiffusion = getGpuInstance();
            stableDiffusion = getCpuInstance();
            NDArray latent = stableDiffusion.inference();

            stableDiffusion.saveImage(latent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
