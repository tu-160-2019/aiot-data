package com.ai.model;

import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.ai.util.ImageUtil;
import com.ai.util.Onnx;
import com.ai.util.Output;
import org.opencv.core.Mat;

import java.util.*;

public class YoloV8 extends Onnx {

    /**
     * 初始化
     *
     * @param labels     模型分类标签
     * @param model_path 模型路径
     * @param gpu        是否开启gou
     * @throws OrtException
     */
    public YoloV8(String[] labels, String model_path, boolean gpu) throws OrtException {
        super(labels, model_path, gpu);
    }

    @Override
    public List<Output> postprocess(OrtSession.Result result, Mat img) throws OrtException {

        float[][] outputData = ((float[][][]) result.get(0).getValue())[0];
        outputData = ImageUtil.transposeMatrix(outputData);
        Map<Integer, List<float[]>> class2Bbox = new HashMap<>();
        for (float[] bbox : outputData) {
            float[] conditionalProbabilities = Arrays.copyOfRange(bbox, 4, outputData.length);
            int label = ImageUtil.argmax(conditionalProbabilities);
            float conf = conditionalProbabilities[label];
            if (conf < confThreshold) continue;

            bbox[4] = conf;

            ImageUtil.xywh2xyxy(bbox);

            if (bbox[0] >= bbox[2] || bbox[1] >= bbox[3]) continue;

            class2Bbox.putIfAbsent(label, new ArrayList<>());
            class2Bbox.get(label).add(bbox);
        }
        List<Output> outputList = new ArrayList<>();
        for (Map.Entry<Integer, List<float[]>> entry : class2Bbox.entrySet()) {
            List<float[]> bboxes = entry.getValue();
            bboxes = ImageUtil.nonMaxSuppression(bboxes, this.nmsThreshold);
            for (float[] x : bboxes) { //预处理进行了缩放，后处理要放大回来

                double x0 = (x[0] - this.dw) / this.ratio;

                double y0 = (x[1] - this.dh) / this.ratio;

                double x1 = (x[2] - this.dw) / this.ratio;

                double y1 = (x[3] - this.dh) / this.ratio;

                Output output = new DetectionOutput(1, (int) x0, (int) y0, (int) x1, (int) y1, entry.getKey(), x[4], labels[entry.getKey()]);
                outputList.add(output);
            }
        }
        return outputList;
    }
}
