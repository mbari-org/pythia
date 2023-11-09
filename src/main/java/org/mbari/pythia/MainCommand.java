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

import io.quarkus.runtime.Quarkus;
import java.nio.file.Path;
import picocli.CommandLine;

/**
 * This is the command line parser. On start up it captures the paths to the
 * model and the names file. These are later used by the Yolov5EventQueueProvider
 * to initialize the event queue.
 */
@CommandLine.Command
public class MainCommand implements Runnable {

    @CommandLine.Parameters(
            index = "0",
            description = {"Path to the torchscript model"})
    Path modelPath;

    @CommandLine.Parameters(index = "1", description = "Path to the names file")
    Path namesPath;

    @Override
    public void run() {
        // https://github.com/quarkusio/quarkus/issues/24204
        Quarkus.waitForExit(); // MUST HAVE THIS
    }
}
