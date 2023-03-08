package org.mbari.amphiaraus.services;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

import java.io.IOException;
import java.nio.file.Path;

public class StockYolov5Service {

  private final Criteria criteria;

  public StockYolov5Service(Path modelPath) {
    this.criteria = buildCriteria(modelPath);
  }

  private static Criteria buildCriteria(Path modelPath) {

    Pipeline pipeline = new Pipeline();
    pipeline.add(new Resize(640));
    pipeline.add(new ToTensor());

    Translator<Image, DetectedObjects> translator = YoloV5Translator
      .builder()
      .setPipeline(pipeline)
      .set
      .build();

    return Criteria.builder()
      .optApplication(Application.CV.OBJECT_DETECTION)
      .setTypes(Image.class, DetectedObjects.class)
      .optModelPath(modelPath)
      .optTranslator(translator)
      .optEngine("PyTorch")
      .build();
  }

  public DetectedObjects predict(Path imageFile) throws IOException, ModelException, TranslateException {
    Image img = ImageFactory.getInstance().fromFile(imageFile);

    try (ZooModel<Image, DetectedObjects> model = criteria.loadModel()) {
      try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
        DetectedObjects detection = predictor.predict(img);
        return detection;
      }
    }
  }
}
