package org.mbari.pythia.services;

import org.junit.jupiter.api.Test;
import org.mbari.pythia.util.ImageUtil;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mbari.pythia.util.ResourceUtil.locateResource;

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
        assertTrue(!boxes.isEmpty());
        System.out.println(boxes);
        ImageUtil.saveBoundingBoxes(imagePath, Paths.get("./target/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b-detections.png"), boxes);


    }



}
