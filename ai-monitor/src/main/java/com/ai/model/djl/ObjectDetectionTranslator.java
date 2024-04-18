package com.ai.model.djl;

import ai.djl.Model;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.ndarray.NDArray;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 图像数据后处理
 */
public abstract class ObjectDetectionTranslator extends BaseImageTranslator<DetectedObjects> {

    protected float threshold;
    private SynsetLoader synsetLoader;
    protected List<String> classes;
    protected double imageWidth;
    protected double imageHeight;

    protected ObjectDetectionTranslator(ObjectDetectionBuilder<?> builder) {
        super(builder);
        this.threshold = builder.threshold;
        this.synsetLoader = builder.synsetLoader;
        this.imageWidth = builder.imageWidth;
        this.imageHeight = builder.imageHeight;
    }

    @Override
    public void prepare(TranslatorContext ctx) throws IOException {
        Model model = ctx.getModel();
        if (classes == null) {
            classes = synsetLoader.load(model);
        }
    }

    @SuppressWarnings("rawtypes")
    public abstract static class ObjectDetectionBuilder<T extends ObjectDetectionBuilder>
            extends ClassificationBuilder<T> {

        protected float threshold = 0.2f;
        protected double imageWidth;
        protected double imageHeight;

        /**
         * 设置预测精度的阈值。
         */
        public T optThreshold(float threshold) {
            this.threshold = threshold;
            return self();
        }

        /**
         * 设置可选的缩放大小。
         */
        public T optRescaleSize(double imageWidth, double imageHeight) {
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            return self();
        }

        /**
         * 调整图像宽度
         */
        public double getImageWidth() {
            return imageWidth;
        }

        /**
         * 调整图像高度
         */
        public double getImageHeight() {
            return imageHeight;
        }

        @Override
        protected void configPostProcess(Map<String, ?> arguments) {
            super.configPostProcess(arguments);
            if (getBooleanValue(arguments, "rescale", false)) {
                optRescaleSize(width, height);
            }
            threshold = getFloatValue(arguments, "threshold", 0.2f);
        }
    }
}

