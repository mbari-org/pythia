package org.mbari.amphiaraus.demos;

import org.mbari.amphiaraus.services.Yolov5Service;

import java.nio.file.Paths;

public class Yolov5ServiceDemo {

  public static void main(String[] args) throws Exception {
    var imagePath = Paths.get(args[0]);
    var modelPath = Paths.get(args[1]);
    var namesPath = Paths.get(args[2]);
    var service = new Yolov5Service(modelPath, namesPath);
    var detectedObjects = service.predict(imagePath);
    System.out.println(detectedObjects);
  }
}
