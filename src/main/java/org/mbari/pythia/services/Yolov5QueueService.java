package org.mbari.pythia.services;


import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.ZooModel;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.domain.PredictionResults;

import java.nio.file.Path;
import java.util.concurrent.*;

// TODO look at EventLoop implementation in vert.x book to see if it's more useful
public class Yolov5QueueService {

    private record Submission(CompletableFuture<PredictionResults> future, Image image) {
        Submission(Image image) {
            this(new CompletableFuture<>(), image);
        }
    }

    public final Path modelPath;
    public final Path namesPath;
    private final BlockingQueue<Submission> queue = new LinkedBlockingQueue<>(100);
    private Thread thread;
    private volatile boolean ok = false;


    public Yolov5QueueService(Path modelPath, Path namesPath) {
        this.modelPath = modelPath;
        this.namesPath = namesPath;
    }

    public CompletableFuture<PredictionResults> predict(Image img) {
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

    private void run() {
        Runnable runnable = () -> {
            ok = true;
            var criteria = Yolov5Service.buildCriteria(modelPath, namesPath);
            try(ZooModel<Image, DetectedObjects> model = criteria.loadModel()) {
                try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
                    Submission submission = null;
                    while (ok) {
                        try {
                            submission = queue.poll(3600L, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException ie) {
                        }
                        if (submission != null) {
                            var detectedObjects = predictor.predict(submission.image);
                            var boundingBoxes = BoundingBox.fromYolov5DetectedObjects(submission.image.getWidth(),
                                    submission.image.getHeight(),
                                    detectedObjects);
                            var predictionResults = new PredictionResults(submission.image, boundingBoxes);
                            submission.future.complete(predictionResults);
                        }
                    }
                }
            }
            catch (Exception e) {
                ok = false;
            }
        };

        thread = new Thread(runnable, getClass().getName());
        thread.setDaemon(true);
        thread.start();

    }



}
