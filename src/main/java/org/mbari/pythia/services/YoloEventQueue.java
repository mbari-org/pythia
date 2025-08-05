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

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.ZooModel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.domain.PredictResults;
import org.mbari.pythia.util.TimeUtil;

/**
 * DJL can do multi-threading, but it depends on the underlying engine.
 * Here, we hedge our bets and just do the prediction in a single thread,
 * reusing the predictor to allow for optimizations.
 * More details at <a href="http://docs.djl.ai/docs/development/inference_performance_optimization.html">...</a>
 */
public class YoloEventQueue {

    private System.Logger log = System.getLogger(getClass().getName());

    private record Submission(CompletableFuture<PredictResults> future, Image image) {
        Submission(Image image) {
            this(new CompletableFuture<>(), image);
        }
    }

    public final Path modelPath;
    public final Path namesPath;
    private final int resolution;
    private final int yoloVersion;
    private final float yoloThreshold; // default threshold
    private final BlockingQueue<Submission> queue = new LinkedBlockingQueue<>(100);
    private Thread thread;
    private volatile boolean ok = false;


    public YoloEventQueue(Path modelPath, Path namesPath, int resolution, int yoloVersion, float yoloThreshold) {
        this.modelPath = modelPath;
        this.namesPath = namesPath;
        this.resolution = resolution;
        this.yoloVersion = yoloVersion;
        this.yoloThreshold = yoloThreshold;
    }

    public CompletableFuture<PredictResults> predict(Path imagePath) {
        try {
            var img = ImageFactory.getInstance().fromFile(imagePath);
            return predict(img);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<PredictResults> predict(Image img) {
        // Load the model and start the server if needed
        if (!ok) {
            run();
        }
        var submission = new Submission(img);
        var added = queue.offer(submission);
        if (!added) {
            submission.future.completeExceptionally(new RuntimeException("Too many images in the prediction queue"));
        }
        return submission.future();
    }

    public boolean isOk() {
        return ok;
    }

    public int pendingCount() {
        return queue.size();
    }

    public void run() {
        if (!ok) {
            Runnable runnable = () -> {
                log.log(System.Logger.Level.INFO, "Starting predictor thread using model at " + modelPath);
                ok = true;
                var criteria = YoloService.buildCriteria(modelPath, namesPath, resolution, yoloVersion, yoloThreshold);
                try (ZooModel<Image, DetectedObjects> model = criteria.loadModel()) {
                    try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
                        Submission submission = null;
                        while (ok) {
                            try {
                                submission = queue.poll(3600L, TimeUnit.SECONDS);
                            } catch (InterruptedException ie) {
                            }
                            if (submission != null) {
                                var detectedObjects = predictor.predict(submission.image);
                                var boundingBoxes = BoundingBox.fromYoloDetectedObjects(
                                        submission.image.getWidth(), submission.image.getHeight(), detectedObjects, resolution);
                                var predictionResults = new PredictResults(submission.image, boundingBoxes);
                                submission.future.complete(predictionResults);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.log(System.Logger.Level.ERROR, "The event loop crashed", e);
                    ok = false;
                }
            };

            thread = new Thread(runnable, getClass().getSimpleName() + "-" + TimeUtil.now());
            thread.setDaemon(true);
            thread.start();
        }
    }
}
