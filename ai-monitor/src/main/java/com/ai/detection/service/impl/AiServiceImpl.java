package com.ai.detection.service.impl;

import com.ai.detection.service.AiService;
import com.ai.util.Onnx;
import com.ai.util.Output;
import com.ai.model.YoloV7;
import net.srt.framework.common.utils.file.FileUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import com.ai.model.*;
import com.ai.config.AiConfig;

@Service
public class AiServiceImpl implements AiService {

    static String model_path = "";

    static String test_img = "";

    static String[] names = {
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

    @Override
    public String yolo7(String filepath) {
//        model_path = ClassLoader.getSystemResource("model/yolov7-tiny.onnx").getPath();
        model_path = "/bigdata/deploy/thinglinks/model/yolov7-tiny.onnx";
        test_img = AiConfig.localFilePath + "/" + filepath.replace(AiConfig.domain + AiConfig.prefix + "/", "");
        String[] tmp = test_img.split("\\.");
        String savePath = tmp[0] + "1." + tmp[1];

        String name = FileUtils.getName(filepath);
        name = name.split("\\.")[0] + "1." + name.split("\\.")[1];
        String resultfile = filepath.replace(FileUtils.getName(filepath), name);
        try {
            // 全局new一次即可，千万不要每次使用都new。可以使用@Bean，或者在spring项目启动时初始化一次即可
            Onnx onnx = new YoloV7(names, model_path, false);

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

    public String yolo5(String filepath) {
        model_path = ClassLoader.getSystemResource("model/helmet_1_25200_n.onnx").getPath();
        test_img = AiConfig.localFilePath + "/" + filepath.replace(AiConfig.domain + AiConfig.prefix + "/", "");
        String[] tmp = test_img.split("\\.");
        String savePath = tmp[0] + "1." + tmp[1];

        String name = FileUtils.getName(filepath);
        name = name.split("\\.")[0] + "1." + name.split("\\.")[1];
        String resultfile = filepath.replace(FileUtils.getName(filepath), name);
        try {
            // 全局new一次即可，千万不要每次使用都new。可以使用@Bean，或者在spring项目启动时初始化一次即可
            Onnx onnx = new YoloV5(names, model_path, false);

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

    public String yolo8(String filepath) {
        model_path = ClassLoader.getSystemResource("model/yolov8s.onnx").getPath();
        test_img = AiConfig.localFilePath + "/" + filepath.replace(AiConfig.domain + AiConfig.prefix + "/", "");
        String[] tmp = test_img.split("\\.");
        String savePath = tmp[0] + "1." + tmp[1];

        String name = FileUtils.getName(filepath);
        name = name.split("\\.")[0] + "1." + name.split("\\.")[1];
        String resultfile = filepath.replace(FileUtils.getName(filepath), name);
        try {
            // 全局new一次即可，千万不要每次使用都new。可以使用@Bean，或者在spring项目启动时初始化一次即可
            Onnx onnx = new YoloV8(names, model_path, false);

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

}


