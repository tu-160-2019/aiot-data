package com.ai.model;

/**
 * 车牌识别输出
 */
public class LicenseOutput extends DetectionOutput {

    /**
     * 车牌颜色
     */
    private String color;

    public LicenseOutput(Integer batchId, Integer x0, Integer y0, Integer x1, Integer y1, Integer clsId, Float score, String name) {
        super(batchId, x0, y0, x1, y1, clsId, score, name);
    }


}
