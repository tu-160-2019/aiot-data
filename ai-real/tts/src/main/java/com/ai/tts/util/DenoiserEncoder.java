package com.ai.tts.util;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DenoiserEncoder {
    public DenoiserEncoder() {
    }

    public Criteria<NDArray, NDArray> criteria(String modelPath) {
        Criteria<NDArray, NDArray> criteria =
                Criteria.builder()
                        .setTypes(NDArray.class, NDArray.class)
                        .optModelPath(Paths.get(modelPath + "/denoiser.zip"))
                        .optTranslator(new DenoiserTranslator())
                        .optEngine("PyTorch") // Use PyTorch engine
                        .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .build();

        return criteria;
    }
}
