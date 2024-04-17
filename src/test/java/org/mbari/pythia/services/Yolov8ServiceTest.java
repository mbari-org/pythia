package org.mbari.pythia.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mbari.pythia.util.ResourceUtil.locateResource;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.mbari.pythia.util.ImageUtil;

public class Yolov8ServiceTest {

    @Test
    public void testPredict() throws Exception {
        // Locate image
        var imagePath = locateResource("/images/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b.jpg");

        // Locate model
        var modelPath = locateResource("/models/mbari401k_yolov8.torchscript");
        var namesPath = locateResource("/models/mbari401k_yolov8.names");
        var service = new Yolov5Service(modelPath, namesPath, 640, 8);
        var boxes = service.predict(imagePath);
        assertTrue(!boxes.isEmpty());
        System.out.println(boxes);
        ImageUtil.saveBoundingBoxes(
                imagePath, Paths.get("./target/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b-detections-8.png"), boxes);
    }
}
