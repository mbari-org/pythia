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
package org.mbari.pythia.util;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.domain.PredictResults;

public class ImageUtil {

    /**
     * Saves the image to the target file. The format of the saved file is determined by it's
     * extension
     *
     * @param image The image to save
     * @param target The file to save the image to.
     * @throws IOException
     */
    public static void saveImage(RenderedImage image, Path target) throws IOException {

        /*
         * Extract the type from the extension
         */
        String name = target.getFileName().toString();
        int dotIdx = name.lastIndexOf(".");
        String ext = name.substring(dotIdx + 1);
        ImageIO.write(image, ext, target.toFile());
    }

    public static void drawBoundingBoxes(BufferedImage image, List<BoundingBox> boxes) {
        var g = (Graphics2D) image.getGraphics();
        for (var b : boxes) {
            var rect = new Rectangle2D.Double(b.x(), b.y(), b.width(), b.height());
            g.draw(rect);
            g.drawString(b.concept(), (int) b.x(), (int) b.y());
        }
    }

    public static void saveBoundingBoxes(Path imagePath, Path outputImagePath, List<BoundingBox> boxes)
            throws IOException {
        var bufferedImage = ImageIO.read(Files.newInputStream(imagePath));
        drawBoundingBoxes(bufferedImage, boxes);
        ImageUtil.saveImage(bufferedImage, outputImagePath);
    }

    public static void saveBoundingBoxes(Path outputImagePath, PredictResults predictResults) throws IOException {
        var bufferedImage = (BufferedImage) predictResults.image().getWrappedImage();
        drawBoundingBoxes(bufferedImage, predictResults.boundingBoxes());
        ImageUtil.saveImage(bufferedImage, outputImagePath);
    }
}
