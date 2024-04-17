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

import static org.junit.jupiter.api.Assertions.*;
import static org.mbari.pythia.util.ResourceUtil.locateResource;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.mbari.pythia.util.ImageUtil;

public class YoloServiceTest {

    @Test
    public void testYolov5() throws Exception {
        runPredict("/models/mbari-mb-benthic-33k.torchscript", "/models/mbari-mb-benthic-33k.names", 5, 640);
    }

    @Test
    public void testYolov8() throws Exception {
        runPredict("/models/mbari401k_yolov8.torchscript", "/models/mbari401k_yolov8.names", 8, 640);
    }

    public void runPredict(String modelFile, String namesFile, int yoloVersion, int resolution) throws Exception {
        // Locate image
        var imagePath = locateResource("/images/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b.jpg");

        // Locate model
        var modelPath = locateResource(modelFile);
        var namesPath = locateResource(namesFile);
        var service = new YoloService(modelPath, namesPath, resolution, yoloVersion);
        var boxes = service.predict(imagePath);
        assertTrue(!boxes.isEmpty());
        System.out.println(boxes);
        ImageUtil.saveBoundingBoxes(
                imagePath, Paths.get("./target/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b-detections-" + yoloVersion + ".png"), boxes);
    }
}
