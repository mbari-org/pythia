package org.mbari.pythia;

import io.smallrye.mutiny.Uni;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.domain.PredictionResults;
import org.mbari.pythia.services.Yolov5QueueService;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/predict")
public class PredictResource {

    @Inject
    Yolov5QueueService yolov5;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<BoundingBox>> predict() {
        //TODO implement this   

        return List.of();
    }
}
