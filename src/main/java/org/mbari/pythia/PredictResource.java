package org.mbari.pythia;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.domain.PredictionResults;
import org.mbari.pythia.services.Yolov5QueueService;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/predict")
public class PredictResource {

    @Inject
    Yolov5QueueService yolov5;

    // https://quarkus.io/guides/context-propagation
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor managedExecutor;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<BoundingBox>> predict(@RestForm("file") FileUpload file) {
        // https://quarkus.io/guides/resteasy-reactive#handling-multipart-form-data
        var imagePath = file.uploadedFile();
        System.out.println("");
//        return CompletableFuture.completedFuture(List.of());

//        return Uni.createFrom()
//                .completionStage(yolov5.predict(imagePath)
//                        .thenApply(PredictionResults::boundingBoxes))
//                .emitOn(Infrastructure.getDefaultExecutor());

        return threadContext.withContextCapture(
                yolov5.predict(imagePath)
                        .thenApplyAsync(PredictionResults::boundingBoxes, managedExecutor));
    }
}
