/*
 * Copyright 2023 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mbari.pythia.services;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.modality.cv.translator.YoloV8Translator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.util.NamesUtil;

/**
 * This service builds the criteria used to load a model. It can also run one-off predictions
 * using the `predict` method, but for production, use the YoloEventQueue for
 * predictions as it will give much better performance.
 */
public class YoloService {

    private static final System.Logger log = System.getLogger(YoloService.class.getName());

    private final Criteria<Image, DetectedObjects> criteria;
    private final int resolution;

    /**
     *
     * @param modelPath The path to a torchscript file. You can use yolov5 to convert a pt file to torchscript
     * @param namesPath The path the names files. Names used to train model
     * @param resolution The resolution to scale the image to before running the model
     */
    public YoloService(Path modelPath, Path namesPath, int resolution) {
        this(modelPath, namesPath, resolution, 5);
    }

    /**
     * Constructor to build a YoloService
     * @param modelPath The path to a torchscript file. You can use yolov5 to convert a pt file to torchscript
     * @param namesPath The path the names files. Names used to train model
     * @param resolution The resolution to scale the image to before running the model
     * @param yoloVersion The version of the Yolov5 model to use. 5 or 8
     */
    public YoloService(Path modelPath, Path namesPath, int resolution, int yoloVersion) {
        this.resolution = resolution;
        this.criteria = buildCriteria(modelPath, namesPath, resolution, yoloVersion);
    }

    /**
     * Method to assemble a criteria used to build a model.
     * @param modelPath The path to the torchscript file
     * @param namesPath The path to the names file
     * @return Criteria that can be used to obtain a predictor
     */
    public static Criteria<Image, DetectedObjects> buildCriteria(Path modelPath, Path namesPath, int resolution, int yoloVersion) {

        log.log(System.Logger.Level.INFO, 
            "Building yolov{0} criteria for model: {1}", yoloVersion, modelPath);

        var names = NamesUtil.load(namesPath);

        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(resolution)); // resolution should match imgsz in the model
        pipeline.add(new ToTensor());

        Translator<Image, DetectedObjects> translator;
        if (yoloVersion == 8) {
            System.out.println("Using Yolov8");
            translator = new YoloV8Translator.Builder()
                    .setPipeline(pipeline)
                    .optSynset(names)
                    .build();
        }
        else {
            translator = YoloV5Translator.builder()
                    .setPipeline(pipeline)
                    .optSynset(names)
                    .build();
        }

        return Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(Image.class, DetectedObjects.class)
                .optModelPath(modelPath)
                .optTranslator(translator)
                .optEngine("PyTorch")
                .build();
    }

    public List<BoundingBox> predict(Path imageFile) throws IOException, ModelException, TranslateException {
        Image img = ImageFactory.getInstance().fromFile(imageFile);

        try (ZooModel<Image, DetectedObjects> model = criteria.loadModel()) {
            try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
                // detections are scaled to resolution x resolution. We have to scale the boxes back to image coords
                DetectedObjects detection = predictor.predict(img);
                return BoundingBox.fromYolov5DetectedObjects(img.getWidth(), img.getHeight(), detection, resolution);
            }
        }
    }
}
