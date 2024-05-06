package com.ai.real.detection.service.impl;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.ai.real.detection.service.AiRealService;
import com.ai.common.model.Onnx;
import com.ai.common.model.Output;
import com.ai.common.model.YoloV7;
import com.ai.common.model.CameraDetection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.srt.framework.common.utils.file.FileUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;

import com.ai.common.model.*;
import com.ai.common.config.AiConfig;

@Slf4j
@Service("aiRealService")
public class AiRealServiceImpl implements AiRealService {

    static String model_path = "";

    static String test_img = "";

    @Autowired
    AiConfig aiConfig;

    CameraDetection cameraDetection;

    @Override
    public String cameraDetection(String filepath) {
        // 将url替换为真实存储路径
        // 注意，只在使用本地存储时后面的upload才有效，如果不是，此句需要修改
        test_img = aiConfig.getPath() + "/" + filepath.replace(aiConfig.getDomain() + aiConfig.getPrefix() + "/upload/", "");
        String extendname = FileUtils.getFileExtendFullName(test_img);
        String savePath = test_img.replace(extendname, "1" + extendname);

        String resultfile = filepath.replace(extendname, "1" + extendname);
        try {
            Onnx onnx = getCameraDetectionInstance();
            VideoCapture video = new VideoCapture();
            // 也可以设置为rtmp或者rtsp视频流：video.open("rtmp://192.168.1.100/live/test"), 海康，大华，乐橙，宇视，录像机等等
            // video.open("rtsp://192.168.1.100/live/test")
            // 也可以静态视频文件：video.open("video/car3.mp4");  flv 等
            // video.open("images/car2.mp4"); //不开启gpu比较卡
            // 不持支h265视频编码，如果无法播放或者程序卡住，请修改视频编码格式或添加ffmpeg库
            video.open(0);  //获取电脑上第0个摄像头
            // 可以把识别后的视频在通过rtmp转发到其他流媒体服务器，就可以远程预览处理后的视频，需要使用ffmpeg将连续图片合成flv 等等
            // 这里先实现检测，推流的事需要的时候在整
            if (!video.isOpened()) {
                System.err.println("打开视频流失败,未检测到监控,请先用vlc软件测试链接是否可以播放！下面使用默认测试视频进行预览效果！");
                video.open("video/car3.mp4");
            }
            // 这里可以根据视频的size来计算并设置画框和字体的大小
            int minDwDh = Math.min((int)video.get(Videoio.CAP_PROP_FRAME_WIDTH), (int)video.get(Videoio.CAP_PROP_FRAME_HEIGHT));

            Mat img = new Mat();
            // 跳帧检测，一般设置为3，毫秒内视频画面变化是不大的，快了无意义，反而浪费性能
            int detect_skip = 4;
            // 跳帧计数
            int detect_skip_index = 1;
            // 最新一帧也就是上一帧推理结果
            float[][] outputData   = null;
            //当前最新一帧。上一帧也可以暂存一下
            Mat image;
            // 使用多线程和GPU可以提升帧率，一个线程拉流，一个线程模型推理，中间通过变量或者队列交换数据,代码示例仅仅使用单线程
            while (video.read(img)) {
                if ((detect_skip_index % detect_skip == 0) || outputData == null){
                    image = img.clone();

                    detect_skip_index = 1;

                    // 运行推理
                    // 模型推理本质是多维矩阵运算，而GPU是专门用于矩阵运算，占用率低，如果使用cpu也可以运行，可能占用率100%属于正常现象，不必纠结。
                    List<Output> outputs = onnx.run(image);
                    // 调用此方法本地查看图片效果
                    Mat resultImage = onnx.drawprocess(outputs, img, savePath);
                    // 如果有什么业务逻辑在这里写即可
                }else{
                    detect_skip_index = detect_skip_index + 1;
                }
            }
            // 关闭视频
            video.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultfile;
    }

    private Onnx getCameraDetectionInstance() {
        String[] names = {
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
        if(cameraDetection == null) {
            try{
                cameraDetection = new CameraDetection(names, ClassLoader.getSystemResource("model/yolov7-tiny.onnx").getPath(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cameraDetection;
    }
}


