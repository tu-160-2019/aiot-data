package com.ai.detection.service.impl;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import com.ai.config.AiConfig;
import com.ai.model.djl.DjlImageUtils;
import com.ai.model.djl.FireSmokeDetect;
import com.ai.detection.service.DjlService;

import lombok.extern.slf4j.Slf4j;

import net.srt.framework.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
}


