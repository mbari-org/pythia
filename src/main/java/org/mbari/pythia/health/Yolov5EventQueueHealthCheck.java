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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.mbari.pythia.services.Yolov5EventQueue;

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

        } else {
            return HealthCheckResponse.down(RESPONSE);
        }
    }
}
