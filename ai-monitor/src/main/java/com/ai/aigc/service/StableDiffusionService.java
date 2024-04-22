package com.ai.aigc.service;

import ai.djl.ndarray.NDArray;

public interface StableDiffusionService {
    public String text2img(String text) ;

    public String img2img(String filePath, String text) ;
}
