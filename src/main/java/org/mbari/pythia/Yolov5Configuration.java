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
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Produces;
import org.mbari.pythia.services.Yolov5EventQueue;
import picocli.CommandLine;

@ApplicationScoped
public class Yolov5Configuration {

    @Produces
    @ApplicationScoped
    Yolov5EventQueue volov5Service(CommandLine.ParseResult parseResult) {
        Path modelPath = parseResult.matchedPositional(0).getValue();
        Path namesPath = parseResult.matchedPositional(1).getValue();
        var service = new Yolov5EventQueue(modelPath, namesPath);
        service.run(); // start the event queue
        return service;
    }
}
