package com.ai.model;

import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.ai.util.Onnx;
import com.ai.util.Output;
import org.opencv.core.Mat;

import java.util.List;

/**
 * paddlepaddle 目标检测模型
 */
public class PaddleDetection extends Onnx {
    /**
     * 初始化
     *
     * @param labels     模型分类标签
     * @param model_path 模型路径
     * @param gpu        是否开启gou
     * @throws OrtException
     */
    public PaddleDetection(String[] labels, String model_path, boolean gpu) throws OrtException {
        super(labels, model_path, gpu);
    }

    @Override
    public List<Output> postprocess(OrtSession.Result result, Mat img) throws OrtException {
        return null;
    }
}
