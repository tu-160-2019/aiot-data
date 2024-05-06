package com.ai.common.model;

import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.ai.common.model.Onnx;
import com.ai.common.model.Output;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class YoloV7 extends Onnx {

    /**
     * 初始化
     *
     * @param labels     模型分类标签
     * @param model_path 模型路径
     * @param gpu        是否开启gou
     * @throws OrtException
     */
    public YoloV7(String[] labels, String model_path, boolean gpu) throws OrtException {
        super(labels, model_path, gpu);
    }

    @Override
    public List<Output> postprocess(OrtSession.Result result, Mat img) throws OrtException {
        float[][] outputData = (float[][]) result.get(0).getValue();
        List<Output> outputList = new ArrayList<>();
        for (float[] x : outputData) { //预处理进行了缩放，后处理要放大回来

            double x0 = (x[1] - this.dw) / this.ratio;

            double y0 = (x[2] - this.dh) / this.ratio;

            double x1 = (x[3] - this.dw) / this.ratio;

            double y1 = (x[4] - this.dh) / this.ratio;

            Output output = new DetectionOutput((int) x[0], (int) x0, (int) y0, (int) x1, (int) y1, (int) x[5], x[6], labels[(int) x[5]]);
            outputList.add(output);
        }
        return outputList;
    }
}
