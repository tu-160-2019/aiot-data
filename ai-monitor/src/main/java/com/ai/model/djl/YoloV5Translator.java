package com.ai.model.djl;

import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.translate.TranslatorContext;

import java.util.*;

public class YoloV5Translator extends ObjectDetectionTranslator {

    private YoloOutputType yoloOutputLayerType;

    private float nmsThreshold;

    protected YoloV5Translator(Builder builder) {
        super(builder);
        yoloOutputLayerType = builder.outputType;
        nmsThreshold = builder.nmsThreshold;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Map<String, ?> arguments) {
        Builder builder = new Builder();
        builder.configPreProcess(arguments);
        builder.configPostProcess(arguments);
        return builder;
    }

    protected double boxIntersection(Rectangle a, Rectangle b) {
        double w = overlap(
                        (a.getX() * 2 + a.getWidth()) / 2, a.getWidth(),
                        (b.getX() * 2 + b.getWidth()) / 2, b.getWidth()
                );
        double h = overlap(
                        (a.getY() * 2 + a.getHeight()) / 2, a.getHeight(),
                        (b.getY() * 2 + b.getHeight()) / 2, b.getHeight()
                );
        if (w < 0 || h < 0) {
            return 0;
        }
        return w * h;
    }

    protected double boxIou(Rectangle a, Rectangle b) {
        return boxIntersection(a, b) / boxUnion(a, b);
    }

    protected double boxUnion(Rectangle a, Rectangle b) {
        double i = boxIntersection(a, b);
        return (a.getWidth()) * (a.getHeight()) + (b.getWidth()) * (b.getHeight()) - i;
    }

    protected DetectedObjects nms(List<IntermediateResult> list) {
        List<String> retClasses = new ArrayList<>();
        List<Double> retProbs = new ArrayList<>();
        List<BoundingBox> retBB = new ArrayList<>();

        for (int k = 0; k < classes.size(); k++) {
            // 1.找出每个类别的最大置信度
            PriorityQueue<IntermediateResult> pq =
                    new PriorityQueue<>(
                            50,
                            (lhs, rhs) -> {
                                // 将置信度高的结果放在队列前面
                                return Double.compare(rhs.getConfidence(), lhs.getConfidence());
                            });

            for (IntermediateResult intermediateResult : list) {
                if (intermediateResult.getDetectedClass() == k) {
                    pq.add(intermediateResult);
                }
            }

            // 2.处理非最大抑制
            while (pq.size() > 0) {
                // 插件感知目标的最大置信度
                IntermediateResult[] a = new IntermediateResult[pq.size()];
                IntermediateResult[] detections = pq.toArray(a);
                Rectangle rec = detections[0].getLocation();
                retClasses.add(detections[0].id);
                retProbs.add(detections[0].confidence);
                retBB.add(
                        new Rectangle(
                                rec.getX() / super.imageWidth,
                                rec.getY() / super.imageHeight,
                                rec.getWidth() / super.imageWidth,
                                rec.getHeight() / super.imageHeight));
                pq.clear();
                for (int j = 1; j < detections.length; j++) {
                    IntermediateResult detection = detections[j];
                    Rectangle location = detection.getLocation();
                    if (boxIou(rec, location) < nmsThreshold) {
                        pq.add(detection);
                    }
                }
            }
        }
        return new DetectedObjects(retClasses, retProbs, retBB);
    }

    protected double overlap(double x1, double w1, double x2, double w2) {
        double l1 = x1 - w1 / 2;
        double l2 = x2 - w2 / 2;
        double left = Math.max(l1, l2);
        double r1 = x1 + w1 / 2;
        double r2 = x2 + w2 / 2;
        double right = Math.min(r1, r2);
        return right - left;
    }

    private DetectedObjects processFromBoxOutput(NDList list) {
        float[] flattened = list.get(0).toFloatArray();
        ArrayList<IntermediateResult> intermediateResults = new ArrayList<>();
        int sizeClasses = classes.size();
        int stride = 5 + sizeClasses;
        int size = flattened.length / stride;
        for (int i = 0; i < size; i++) {
            int indexBase = i * stride;
            float maxClass = 0;
            int maxIndex = 0;
            for (int c = 0; c < sizeClasses; c++) {
                if (flattened[indexBase + c + 5] > maxClass) {
                    maxClass = flattened[indexBase + c + 5];
                    maxIndex = c;
                }
            }
            float score = maxClass * flattened[indexBase + 4];
            if (score > threshold) {
                float xPos = flattened[indexBase];
                float yPos = flattened[indexBase + 1];
                float w = flattened[indexBase + 2];
                float h = flattened[indexBase + 3];
                Rectangle rect = new Rectangle(Math.max(0, xPos - w / 2), Math.max(0, yPos - h / 2), w, h);
                intermediateResults.add(
                        new IntermediateResult(classes.get(maxIndex), score, maxIndex, rect));
            }
        }
        return nms(intermediateResults);
    }

    private DetectedObjects processFromDetectOutput() {
        throw new UnsupportedOperationException("网络层不支持，检查YoloV5导出格式是否正确");
    }

    @Override
    public DetectedObjects processOutput(TranslatorContext ctx, NDList list) {
        switch (yoloOutputLayerType) {
            case DETECT:
                return processFromDetectOutput();
            case AUTO:
                if (list.get(0).getShape().dimension() > 2) {
                    return processFromDetectOutput();
                } else {
                    return processFromBoxOutput(list);
                }
            case BOX:
            default:
                return processFromBoxOutput(list);
        }
    }

    public enum YoloOutputType {
        BOX,
        DETECT,
        AUTO
    }

    public static class Builder extends ObjectDetectionBuilder<Builder> {

        YoloOutputType outputType = YoloOutputType.AUTO;
        float nmsThreshold = 0.4f;

        public Builder optOutputType(YoloOutputType outputType) {
            this.outputType = outputType;
            return this;
        }

        public Builder optNmsThreshold(float nmsThreshold) {
            this.nmsThreshold = nmsThreshold;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        protected void configPostProcess(Map<String, ?> arguments) {
            super.configPostProcess(arguments);
            String type = getStringValue(arguments, "outputType", "AUTO");
            outputType = YoloOutputType.valueOf(type.toUpperCase(Locale.ENGLISH));
            nmsThreshold = getFloatValue(arguments, "nmsThreshold", 0.4f);
        }

        public YoloV5Translator build() {
            // 自定义pipeline匹配默认的yolov5输入层
            if (pipeline == null) {
                addTransform(array -> array.transpose(2, 0, 1).toType(DataType.FLOAT32, false).div(255));
            }
            validate();
            return new YoloV5Translator(this);
        }
    }

    private static final class IntermediateResult {

        /**
         * 识别置信度，越高越好
         */
        private double confidence;

        /** 显示识别的类别名称 */
        private int detectedClass;

        /**
         * 已识别的唯一标识符，特定于class，不是对象实例的。
         */
        private String id;

        /** 源图像中的可选位置，用于识别对象的位置。 */
        private Rectangle location;

        IntermediateResult(String id, double confidence, int detectedClass, Rectangle location) {
            this.confidence = confidence;
            this.id = id;
            this.detectedClass = detectedClass;
            this.location = location;
        }

        public double getConfidence() {
            return confidence;
        }

        public int getDetectedClass() {
            return detectedClass;
        }

        public String getId() {
            return id;
        }

        public Rectangle getLocation() {
            return new Rectangle(
                    location.getX(), location.getY(), location.getWidth(), location.getHeight());
        }
    }
}

