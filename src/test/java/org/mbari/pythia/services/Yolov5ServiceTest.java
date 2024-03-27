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

public class Yolov5ServiceTest {

    @Test
    public void testPredict() throws Exception {
        // Locate image
        var imagePath = locateResource("/images/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b.jpg");

        // Locate model
        var modelPath = locateResource("/models/mbari-mb-benthic-33k.torchscript");
        var namesPath = locateResource("/models/mbari-mb-benthic-33k.names");
        var service = new Yolov5Service(modelPath, namesPath, 640);
        var boxes = service.predict(imagePath);
        assertTrue(!boxes.isEmpty());
        System.out.println(boxes);
        ImageUtil.saveBoundingBoxes(
                imagePath, Paths.get("./target/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b-detections.png"), boxes);
    }
}
