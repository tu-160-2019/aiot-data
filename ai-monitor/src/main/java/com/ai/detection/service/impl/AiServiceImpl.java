package com.ai.detection.service.impl;

import com.ai.detection.service.AiService;
import com.ai.util.Onnx;
import com.ai.util.Output;
import com.ai.model.YoloV7;
import lombok.extern.slf4j.Slf4j;
import net.srt.framework.common.utils.file.FileUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.ai.model.*;
import com.ai.config.AiModelConfig;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    static String model_path = "";

    static String test_img = "";

    @Autowired
    AiModelConfig aiConfig;

    Onnx yolov7;
    Onnx yolov8;

    @Override
    public String yolo7(String filepath) {
        // 将url替换为真实存储路径
        // 注意，只在使用本地存储时后面的upload才有效，如果不是，此句需要修改
        test_img = aiConfig.getPath() + "/" + filepath.replace(aiConfig.getDomain() + aiConfig.getPrefix() + "/upload/", "");
        String extendname = FileUtils.getFileExtendFullName(test_img);
        String savePath = test_img.replace(extendname, "1" + extendname);

        String resultfile = filepath.replace(extendname, "1" + extendname);
        try {
            Onnx onnx = getYolov7Instance();

            // 也可以使用接口收到的base64图像Imgcodecs.imdecode()
            Mat img = Imgcodecs.imread(test_img);

            // 这一步已经结束，可以通过接口返回给前端结果，或者自己循环打印看结果输出
            List<Output> outputs = onnx.run(img.clone());

            // 调用此方法本地查看图片效果
            Mat resultImage = onnx.drawprocess(outputs, img, savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultfile;
    }

    @Override
    public String yolo8(String filepath) {
        // 将url替换为真实存储路径
        // 注意，只在使用本地存储时后面的upload才有效，如果不是，此句需要修改
        test_img = aiConfig.getPath() + "/" + filepath.replace(aiConfig.getDomain() + aiConfig.getPrefix() + "/upload/", "");
        String extendname = FileUtils.getFileExtendFullName(test_img);
        String savePath = test_img.replace(extendname, "1" + extendname);

        String resultfile = filepath.replace(extendname, "1" + extendname);
        try {
            // 全局new一次即可，千万不要每次使用都new。可以使用@Bean，或者在spring项目启动时初始化一次即可
            Onnx onnx = getYolov8Instance();

            // 也可以使用接口收到的base64图像Imgcodecs.imdecode()
            Mat img = Imgcodecs.imread(test_img);

            // 这一步已经结束，可以通过接口返回给前端结果，或者自己循环打印看结果输出
            List<Output> outputs = onnx.run(img.clone());

            // 调用此方法本地查看图片效果
            Mat resultImage = onnx.drawprocess(outputs, img, savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultfile;
    }

    private Onnx getYolov8Instance() {
        String[] labels = {
                "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train",
                "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter",
                "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear",
                "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase",
                "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat",
                "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle",
                "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
                "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut",
                "cake", "chair", "couch", "potted plant", "bed", "dining table", "toilet",
                "tv", "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave",
                "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors",
                "teddy bear", "hair drier", "toothbrush"};

        if(yolov8 == null) {
            try{
                yolov8 = new YoloV8(labels, ClassLoader.getSystemResource("model/yolov8s.onnx").getPath(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yolov8;
    }

    private Onnx getYolov7Instance() {
        String[] names = {"no_helmet", "helmet"};
        if(yolov7 == null) {
            try{
                yolov7 = new YoloV7(names, ClassLoader.getSystemResource("model/helmet_n_7.onnx").getPath(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yolov7;
    }
}


