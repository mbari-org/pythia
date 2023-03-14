package org.mbari.pythia.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.mbari.pythia.services.Yolov5EventQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class Yolov5EventQueueHealthCheck implements HealthCheck {

    Yolov5EventQueue yolov5EventQueue;

    private static final String RESPONSE = "Yolov5 event queue";

    @Inject
    public Yolov5EventQueueHealthCheck(Yolov5EventQueue yolov5EventQueue) {
        this.yolov5EventQueue = yolov5EventQueue;
    }

    @Override
    public HealthCheckResponse call() {
        if (yolov5EventQueue.isOk()) {
            return HealthCheckResponse.named(RESPONSE)
                    .up()
                    .withData("pendingCount", yolov5EventQueue.pendingCount())
                    .build();

        }
        else {
            return HealthCheckResponse.down(RESPONSE);
        }
    }
}
