package org.mbari.pythia;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.domain.PredictResults;
import org.mbari.pythia.services.Yolov5EventQueue;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Path("/predict")
public class PredictResource {

    @Inject
    Yolov5EventQueue yolov5;

    // https://quarkus.io/guides/context-propagation
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor managedExecutor;

    /**
     * Runs inference on an image using Yolov5 and the model provided to the server
     * @param file The image (as a multipart form)
     * @return Any inferred predictions found in the image.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<BoundingBox>> predict(@RestForm("file") FileUpload file) {
        // https://quarkus.io/guides/resteasy-reactive#handling-multipart-form-data
        var imagePath = file.uploadedFile();

        return threadContext.withContextCapture(
                yolov5.predict(imagePath)
                        .thenApplyAsync(PredictResults::boundingBoxes, managedExecutor));
    }
}
