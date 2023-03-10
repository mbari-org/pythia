package org.mbari.amphiaraus.services;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import org.junit.jupiter.api.Test;
import org.mbari.amphiaraus.domain.BoundingBox;
import org.mbari.amphiaraus.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Yolov5ServiceTest {

  @Test
  public void testPredict() throws Exception {
    // Locate image
    var imagePath = locateResource("/images/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b.jpg");

    // Locate model
    var modelPath = locateResource("/models/mbari-mb-benthic-33k.torchscript");
    var namesPath = locateResource("/models/mbari-mb-benthic-33k.names");
    var service = new Yolov5Service(modelPath, namesPath);
    var boxes = service.predict(imagePath);
    System.out.println(boxes);
    ImageUtil.saveBoundingBoxes(imagePath, Paths.get("./target/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b-detections.png"), boxes);


  }

  private Path locateResource(String resource) {
    try {
      var url = getClass().getResource(resource);
      assertNotNull(url, "Could not find " + resource);
      var path = Paths.get(url.toURI());
      assertTrue(Files.exists(path));
      return path;
    }
    catch (Exception e) {
      fail("Unable to find resource `" + resource + "`");
      return null;
    }
  }

}
