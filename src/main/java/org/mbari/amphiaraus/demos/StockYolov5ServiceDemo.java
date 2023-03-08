package org.mbari.amphiaraus.demos;

import org.mbari.amphiaraus.services.StockYolov5Service;

import java.nio.file.Paths;

public class StockYolov5ServiceDemo {

  public static void main(String[] args) throws Exception {
    var imagePath = Paths.get(args[0]);
    var modelPath = Paths.get(args[1]);
    var service = new StockYolov5Service(modelPath);
    var detectedObjects = service.predict(imagePath);
    System.out.println(detectedObjects);
  }
}
