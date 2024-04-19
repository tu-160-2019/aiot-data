package com.ai.model.djl;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class VehicleDetect {

    public VehicleDetect() {}

    public Criteria<Image, DetectedObjects> criteria(String modelPath) {

        Criteria<Image, DetectedObjects> criteria =
                Criteria.builder()
                        .optEngine("PaddlePaddle")
                        .setTypes(Image.class, DetectedObjects.class)
                        .optModelPath(Paths.get(modelPath))
                        .optModelName("inference")
                        .optTranslator(new VehicleTranslator())
                        .optProgress(new ProgressBar())
                        .build();

        return criteria;
    }

    private final class VehicleTranslator implements Translator<Image, DetectedObjects> {
        private int width;
        private int height;
        private List<String> className;

        VehicleTranslator() {}

        @Override
        public void prepare(TranslatorContext ctx) throws IOException {
            Model model = ctx.getModel();
            try (InputStream is = model.getArtifact("label_file.txt").openStream()) {
                className = Utils.readLines(is, true);
                //            classes.add(0, "blank");
                //            classes.add("");
            }
        }

        @Override
        public DetectedObjects processOutput(TranslatorContext ctx, NDList list) {
            return processImageOutput(list);
        }

        @Override
        public NDList processInput(TranslatorContext ctx, Image input) {
            NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
            array = NDImageUtils.resize(array, 608, 608);
            if (!array.getDataType().equals(DataType.FLOAT32)) {
                array = array.toType(DataType.FLOAT32, false);
            }
            array = array.div(255f);
            NDArray mean = ctx.getNDManager().create(new float[] {0.485f, 0.456f, 0.406f}, new Shape(1, 1, 3));
            NDArray std = ctx.getNDManager().create(new float[] {0.229f, 0.224f, 0.225f}, new Shape(1, 1, 3));
            array = array.sub(mean);
            array = array.div(std);

            array = array.transpose(2, 0, 1); // HWC -> CHW RGB
            array = array.expandDims(0);
            width = input.getWidth();
            height = input.getHeight();

            NDArray imageSize = ctx.getNDManager().create(new int[] {height, width});
            imageSize = imageSize.toType(DataType.INT32, false);

            imageSize = imageSize.expandDims(0);

            return new NDList(array, imageSize);
        }

        @Override
        public Batchifier getBatchifier() {
            return null;
        }

        DetectedObjects processImageOutput(NDList list) {
            NDArray result = list.singletonOrThrow();
            float[] probabilities = result.get(":,1").toFloatArray();
            List<String> names = new ArrayList<>();
            List<Double> prob = new ArrayList<>();
            List<BoundingBox> boxes = new ArrayList<>();
            for (int i = 0; i < probabilities.length; i++) {
                float[] array = result.get(i).toFloatArray();

                names.add(className.get((int) array[0]));

                prob.add((double) probabilities[i]);
                // x, y , w , h
                // dt['left'], dt['top'], dt['right'], dt['bottom'] = clip_bbox(bbox, org_img_width, org_img_height)
                boxes.add(
                        new Rectangle(
                                array[2] / width,
                                array[3] / height,
                                (array[4] - array[2]) / width,
                                (array[5] - array[3]) / height));
            }
            return new DetectedObjects(names, prob, boxes);
        }
    }
}

