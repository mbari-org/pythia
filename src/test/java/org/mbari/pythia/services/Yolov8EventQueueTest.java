package org.mbari.pythia.services;

import ai.djl.modality.cv.ImageFactory;
import org.junit.jupiter.api.Test;
import org.mbari.pythia.domain.PredictResults;
import org.mbari.pythia.util.ImageUtil;
import org.mbari.pythia.util.ResourceUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mbari.pythia.util.ResourceUtil.locateResource;

public class Yolov8EventQueueTest {
    @Test
    public void testPredict() throws Exception {
        // Locate image
        var imagePaths = List.of(
                        "/images/112512--1c22e634-2378-4b66-86e4-e00d3ca3ad5b.jpg",
                        "/images/03_00_51_14.jpg",
                        "/images/20191205T174157Z--d90cba26-d8c4-4615-9de2-463fc0eccb83.jpg",
                        "/images/20220504T173613Z--fd64138f-bb97-43bb-8761-cae588c3edae.jpg")
                .stream()
                .map(ResourceUtil::locateResource)
                .toList();

        // Locate model
        var modelPath = locateResource("/models/mbari401k_yolov8.torchscript");
        var namesPath = locateResource("/models/mbari401k_yolov8.names");
        var service = new YoloEventQueue(modelPath, namesPath, 640, 5);
        var imageFactory = ImageFactory.getInstance();

        // Process in service queye
        List<CompletableFuture<PredictResults>> futures = new ArrayList<>();
        for (var p : imagePaths) {
            var f = service.predict(imageFactory.fromFile(p));
            futures.add(f);
        }

        // Process output
        int i = 0;
        for (var f : futures) {
            var j = i;
            f.thenAccept(pr -> {
                        assertNotNull(pr.image());
                        assertNotNull(pr.boundingBoxes());
                        assertFalse(pr.boundingBoxes().isEmpty());
                        var output = Paths.get("./target/image-v8-" + j + ".png");
                        try {
                            ImageUtil.saveBoundingBoxes(output, pr);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .get(10, TimeUnit.SECONDS);
            i++;
        }
    }
}
