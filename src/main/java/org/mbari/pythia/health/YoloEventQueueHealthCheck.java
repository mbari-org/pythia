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
package org.mbari.pythia.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.mbari.pythia.services.YoloEventQueue;

/**
 * Health check at ROOT/q/health. We report the number of images in the event queue waiting
 * to be processed.
 */
@Readiness
@ApplicationScoped
public class YoloEventQueueHealthCheck implements HealthCheck {

    YoloEventQueue yoloEventQueue;

    private static final String RESPONSE = "Yolov5 event queue";

    @Inject
    public YoloEventQueueHealthCheck(YoloEventQueue yoloEventQueue) {
        this.yoloEventQueue = yoloEventQueue;
    }

    @Override
    public HealthCheckResponse call() {
        if (yoloEventQueue.isOk()) {
            return HealthCheckResponse.named(RESPONSE)
                    .up()
                    .withData("pendingCount", yoloEventQueue.pendingCount())
                    .build();

        } else {
            return HealthCheckResponse.down(RESPONSE);
        }
    }
}
