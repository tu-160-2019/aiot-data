package com.ai.detection.service.impl;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.output.Rectangle;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;

import com.ai.config.AiConfig;
import com.ai.model.djl.DjlImageUtils;
import com.ai.model.djl.FireSmokeDetect;
import com.ai.model.djl.ReflectiveVest;
import com.ai.model.djl.VehicleDetect;
import com.ai.model.djl.FaceDetection;
import com.ai.model.djl.FaceMaskDetect;
import com.ai.detection.service.DjlService;

import lombok.extern.slf4j.Slf4j;

import net.srt.framework.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;

@Slf4j
@Service
public class DjlServiceImpl implements DjlService {

    static String model_path = "";

    static String test_img = "";

    @Autowired
    AiConfig aiConfig;

    @Override
    public String fireSmoke(String filepath) {
        test_img = aiConfig.getPath() + "/" + filepath.replace(aiConfig.getDomain() + aiConfig.getPrefix() + "/upload/", "");
        String extendname = FileUtils.getFileExtendFullName(test_img);
        String savePath = test_img.replace(extendname, "1.png");
        String resultfile = filepath.replace(extendname, "1.png");

        try { // 注意：最好系统启动时就提前加载好模型，不要每次都来一次，比较耗时
            Image image = ImageFactory.getInstance().fromUrl(filepath);

            Criteria<Image, DetectedObjects> criteria = new FireSmokeDetect().criteria(aiConfig.getModelPath() + File.separator + "fire_smoke.zip");
            ZooModel model = ModelZoo.loadModel(criteria);
            Predictor<Image, DetectedObjects> predictor = model.newPredictor();

            DetectedObjects detections = predictor.predict(image);
            List<DetectedObjects.DetectedObject> items = detections.items();

            List<String> names = new ArrayList<>();
            List<Double> prob = new ArrayList<>();
            List<BoundingBox> boxes = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                DetectedObjects.DetectedObject item = items.get(i);
                if (item.getProbability() < 0.3f) {
                    continue;
                }
                names.add(item.getClassName() + " " + item.getProbability());
                prob.add(item.getProbability());
                boxes.add(item.getBoundingBox());
            }

            detections = new DetectedObjects(names, prob, boxes);
            DjlImageUtils.saveBoundingBoxImage(image, detections, savePath);

            log.info("{}", detections);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultfile;
    }

    @Override
    public String reflectiveVest(String filepath) {
        test_img = aiConfig.getPath() + "/" + filepath.replace(aiConfig.getDomain() + aiConfig.getPrefix() + "/upload/", "");
        String extendname = FileUtils.getFileExtendFullName(test_img);
        String savePath = test_img.replace(extendname, "1.png");
        String resultfile = filepath.replace(extendname, "1.png");

        try { // 注意：最好系统启动时就提前加载好模型，不要每次都来一次，比较耗时
            Image image = ImageFactory.getInstance().fromUrl(filepath);

            Criteria<Image, DetectedObjects> criteria = new ReflectiveVest().criteria(aiConfig.getModelPath() + File.separator + "reflective_clothes.zip");
            ZooModel model = ModelZoo.loadModel(criteria);
            Predictor<Image, DetectedObjects> predictor = model.newPredictor();

            DetectedObjects detections = predictor.predict(image);
            List<DetectedObjects.DetectedObject> items = detections.items();

            List<String> names = new ArrayList<>();
            List<Double> prob = new ArrayList<>();
            List<BoundingBox> boxes = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                DetectedObjects.DetectedObject item = items.get(i);
                if (item.getProbability() < 0.5f) {
                    continue;
                }
                names.add(item.getClassName() + " " + item.getProbability());
                prob.add(item.getProbability());
                boxes.add(item.getBoundingBox());
            }

            detections = new DetectedObjects(names, prob, boxes);
            DjlImageUtils.saveBoundingBoxImage(image, detections, savePath);

            log.info("{}", detections);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultfile;
    }

    public String vehicle(String filepath) {
        test_img = aiConfig.getPath() + "/" + filepath.replace(aiConfig.getDomain() + aiConfig.getPrefix() + "/upload/", "");
        String extendname = FileUtils.getFileExtendFullName(test_img);
        String savePath = test_img.replace(extendname, "1.png");
        String resultfile = filepath.replace(extendname, "1.png");

        try { // 注意：最好系统启动时就提前加载好模型，不要每次都来一次，比较耗时
            Image image = ImageFactory.getInstance().fromUrl(filepath);
            Criteria<Image, DetectedObjects> criteria = new VehicleDetect().criteria(aiConfig.getModelPath() + File.separator + "vehicle.zip");
            ZooModel model = ModelZoo.loadModel(criteria);
            Predictor<Image, DetectedObjects> predictor = model.newPredictor();

            DetectedObjects detections = predictor.predict(image);

            List<DetectedObjects.DetectedObject> items = detections.items();
            List<String> names = new ArrayList<>();
            List<Double> prob = new ArrayList<>();
            List<BoundingBox> rect = new ArrayList<>();
            for (DetectedObjects.DetectedObject item : items) {
                if (item.getProbability() < 0.55) {
                    continue;
                }
                names.add(item.getClassName());
                prob.add(item.getProbability());
                rect.add(item.getBoundingBox());
            }

            detections = new DetectedObjects(names, prob, rect);

            DjlImageUtils.saveBoundingBoxImage(image, detections, savePath);

            log.info("{}", detections);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultfile;
    }

    public String cameraFacemask(String filepath) {
        test_img = aiConfig.getPath() + "/" + filepath.replace(aiConfig.getDomain() + aiConfig.getPrefix() + "/upload/", "");
        String extendname = FileUtils.getFileExtendFullName(test_img);
        String savePath = test_img.replace(extendname, "1.png");
        String resultfile = filepath.replace(extendname, "1.png");

        float shrink = 0.5f;
        float threshold = 0.7f;
        Criteria<Image, DetectedObjects> criteria = new FaceDetection().criteria(shrink, threshold, aiConfig.getModelPath() + File.separator + "face_detection.zip");
        Criteria<Image, Classifications> maskCriteria = new FaceMaskDetect().criteria(aiConfig.getModelPath() + File.separator + "face_mask.zip");

        try { // 注意：最好系统启动时就提前加载好模型，不要每次都来一次，比较耗时
            // 开启摄像头，获取图像（得到的图像为frame类型，需要转换为mat类型进行检测和识别）
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();

            // Frame与Mat转换
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

            CanvasFrame canvas = new CanvasFrame("Face Detection");
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            canvas.setVisible(true);
            canvas.setFocusable(true);
            // 窗口置顶
            if (canvas.isAlwaysOnTopSupported()) {
                canvas.setAlwaysOnTop(true);
            }
            Frame frame = null;

            ZooModel model = ModelZoo.loadModel(criteria);
            Predictor<Image, DetectedObjects> predictor = model.newPredictor();
            ZooModel classifyModel = ModelZoo.loadModel(maskCriteria);
            Predictor<Image, Classifications> classifier = classifyModel.newPredictor();

            // 获取图像帧
            for (; canvas.isVisible() && (frame = grabber.grab()) != null; ) {

                // 将获取的frame转化成mat数据类型
                Mat img = converter.convert(frame);
                java.awt.image.BufferedImage buffImg = JavaCVImageUtil.mat2BufferedImage(img);

                Image image = ImageFactory.getInstance().fromImage(buffImg);
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                DetectedObjects detections = predictor.predict(image);
                List<DetectedObjects.DetectedObject> items = detections.items();

                // 遍历人脸
                for (DetectedObjects.DetectedObject item : items) {
                    Image subImg = getSubImage(image, item.getBoundingBox());
                    Classifications classifications = classifier.predict(subImg);
                    String className = classifications.best().getClassName();

                    BoundingBox box = item.getBoundingBox();
                    Rectangle rectangle = box.getBounds();
                    int x = (int) (rectangle.getX() * imageWidth);
                    int y = (int) (rectangle.getY() * imageHeight);
                    Rect face =
                            new Rect(
                                    x,
                                    y,
                                    (int) (rectangle.getWidth() * imageWidth),
                                    (int) (rectangle.getHeight() * imageHeight));

                    // 绘制人脸矩形区域，scalar色彩顺序：BGR(蓝绿红)
                    org.bytedeco.opencv.global.opencv_imgproc.rectangle(img, face, new Scalar(0, 0, 255, 1));

                    int pos_x = Math.max(face.tl().x() - 10, 0);
                    int pos_y = Math.max(face.tl().y() - 10, 0);
                    // 在人脸矩形上面绘制文字
                    org.bytedeco.opencv.global.opencv_imgproc.putText(
                            img,
                            className,
                            new Point(pos_x, pos_y),
                            org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_COMPLEX,
                            1.0,
                            new Scalar(0, 0, 255, 2.0));
                }

                // 显示视频图像
                canvas.showImage(frame);

                canvas.dispose();
                grabber.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultfile;
    }

    private static Image getSubImage(Image img, BoundingBox box) {
        Rectangle rect = box.getBounds();
        int width = img.getWidth();
        int height = img.getHeight();
        int[] squareBox =
                extendSquare(
                        rect.getX() * width,
                        rect.getY() * height,
                        rect.getWidth() * width,
                        rect.getHeight() * height,
                        0); // 0.18

        if (squareBox[0] < 0) squareBox[0] = 0;
        if (squareBox[1] < 0) squareBox[1] = 0;
        if (squareBox[0] > width) squareBox[0] = width;
        if (squareBox[1] > height) squareBox[1] = height;
        if ((squareBox[0] + squareBox[2]) > width) squareBox[2] = width - squareBox[0];
        if ((squareBox[1] + squareBox[2]) > height) squareBox[2] = height - squareBox[1];
        return img.getSubImage(squareBox[0], squareBox[1], squareBox[2], squareBox[2]);
    }

    private static int[] extendSquare(
            double xmin, double ymin, double width, double height, double percentage) {
        double centerx = xmin + width / 2;
        double centery = ymin + height / 2;
        double maxDist = Math.max(width / 2, height / 2) * (1 + percentage);
        return new int[] {(int) (centerx - maxDist), (int) (centery - maxDist), (int) (2 * maxDist)};
    }
}


