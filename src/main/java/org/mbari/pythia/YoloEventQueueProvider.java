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
package org.mbari.pythia;

import java.nio.file.Path;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.mbari.pythia.services.YoloEventQueue;
import picocli.CommandLine;

/**
 * Through the magic of EE annotations this wires up the event queue based on caommand-line
 * arguments. You'll never see this class explicitly called anywhere, but whenever a
 * YoloEventQueue is injected, this class is used to produce it as a singleton.
 */
@ApplicationScoped
public class YoloEventQueueProvider {

    @ConfigProperty(name = "yolov5.resolution")
    Integer resolution;

    @ConfigProperty(name = "yolo.version")
    Integer yoloVersion;

    @ConfigProperty(name = "yolo.threshold", defaultValue = "0.05")
    Float threshold;

    @Produces
    @ApplicationScoped
    YoloEventQueue volov5EventQueue(CommandLine.ParseResult parseResult) {
        Path modelPath = parseResult.matchedPositional(0).getValue();
        Path namesPath = parseResult.matchedPositional(1).getValue();
        var service = new YoloEventQueue(modelPath, namesPath, resolution, yoloVersion, threshold);
        service.run(); // start the event queue
        return service;
    }
}
