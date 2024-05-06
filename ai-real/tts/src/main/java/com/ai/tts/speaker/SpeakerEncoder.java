package com.ai.tts.speaker;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpeakerEncoder {
    public SpeakerEncoder() {}

    public Criteria<NDArray, NDArray> criteria(String modePath) {
        Criteria<NDArray, NDArray> criteria =
                Criteria.builder()
                        .setTypes(NDArray.class, NDArray.class)
                        .optModelPath(Paths.get(modePath + "/speakerEncoder.zip"))
                        .optTranslator(new SpeakerEncoderTranslator())
                        .optEngine("PyTorch") // Use PyTorch engine
                        .optDevice(Device.cpu())
                        .optProgress(new ProgressBar())
                        .build();

        return criteria;
    }
}
