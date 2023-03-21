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
package org.mbari.pythia.domain;

import ai.djl.modality.cv.output.DetectedObjects;
import java.util.ArrayList;
import java.util.List;

public record BoundingBox(String concept, double x, double y, double width, double height, double probability) {

    /**
     * Detection output from teh yolov5 detector are done on a
     * 640x640 image and have to be scaled back up.
     * @param concept
     * @param imageWidth
     * @param imageHeight
     * @param detectionX
     * @param detectionY
     * @param detectionWidth
     * @param detectionHeight
     * @param probability
     * @return
     */
    public static BoundingBox fromYolov5(
            String concept,
            int imageWidth,
            int imageHeight,
            double detectionX,
            double detectionY,
            double detectionWidth,
            double detectionHeight,
            double probability) {
        var xScale = imageWidth / 640D;
        var yScale = imageHeight / 640D;
        return new BoundingBox(
                concept,
                detectionX * xScale,
                detectionY * yScale,
                detectionWidth * xScale,
                detectionHeight * yScale,
                probability);
    }

    /**
     * Scales yolov5 detections back to the image size
     * @param imageWidth The original images width
     * @param imageHeight The original images height
     * @param detectedObjects The output from `predict`
     * @return boxes that are scaled apporopriately to the original source image
     */
    public static List<BoundingBox> fromYolov5DetectedObjects(
            int imageWidth, int imageHeight, DetectedObjects detectedObjects) {
        int n = detectedObjects.getNumberOfObjects();
        List<BoundingBox> boxes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            var obj = (DetectedObjects.DetectedObject) detectedObjects.item(i);
            var bb = obj.getBoundingBox();
            var rect = bb.getBounds();
            var boundingBox = BoundingBox.fromYolov5(
                    obj.getClassName(),
                    imageWidth,
                    imageHeight,
                    rect.getX(),
                    rect.getY(),
                    rect.getWidth(),
                    rect.getHeight(),
                    obj.getProbability());
            boxes.add(boundingBox);
        }
        return boxes;
    }
}
