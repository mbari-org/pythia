package org.mbari.pythia.services;


import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.util.NamesUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// /Users/brian/workspace/M3/03_00_51_14.jpg /Users/brian/Documents/M3/models/mbari315k.torchscript /Users/brian/Documents/M3/models/mbari315k.names
public class Yolov5Service {

    private final Criteria<Image, DetectedObjects> criteria;

    /**
     *
     * @param modelPath The path to a torchscript file. You can use yolov5 to convert a pt file to torchscript
     * @param namesPath The path the names files. Names used to train model
     */
    public Yolov5Service(Path modelPath, Path namesPath) {
        this.criteria = buildCriteria(modelPath, namesPath);
    }

    public static Criteria<Image, DetectedObjects> buildCriteria(Path modelPath, Path namesPath) {

        var names = NamesUtil.load(namesPath);

        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(640)); // required for yolov5? Doesn't work without it
        pipeline.add(new ToTensor());

        Translator<Image, DetectedObjects> translator = YoloV5Translator
                .builder()
                .setPipeline(pipeline)
                .optSynsetArtifactName("synset.txt")
                .optSynset(names)
                .build();

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
                // detections are scaled to 640 * 640 image. We have to scale the boxes back to image coords
                DetectedObjects detection = predictor.predict(img);
                return BoundingBox.fromYolov5DetectedObjects(img.getWidth(), img.getHeight(), detection);
            }
        }
    }

}
