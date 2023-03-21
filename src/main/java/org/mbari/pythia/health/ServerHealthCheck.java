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
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.mbari.pythia.AppConfig;

@Liveness
@ApplicationScoped
public class ServerHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        var runtime = Runtime.getRuntime();
        return HealthCheckResponse.named("Server status")
                .up()
                .withData("jdkVersion", Runtime.version().toString())
                .withData("availableProcessors", runtime.availableProcessors())
                .withData("freeMemory", runtime.freeMemory())
                .withData("maxMemory", runtime.maxMemory())
                .withData("totalMemory", runtime.totalMemory())
                .withData("application", AppConfig.NAME)
                .withData("version", AppConfig.VERSION)
                .withData("description", AppConfig.DESCRIPTION)
                .build();
    }
}

/*
jdkVersion = Runtime.version.toString,
      availableProcessors = runtime.availableProcessors,
      freeMemory = runtime.freeMemory,
      maxMemory = runtime.maxMemory,
      totalMemory = runtime.totalMemory

{
  "jdkVersion": "17.0.2+8-86",
  "availableProcessors": 2,
  "freeMemory": 309504656,
  "maxMemory": 4294967296,
  "totalMemory": 478150656,
  "application": "annosaurus",
  "version": "0.13.1",
  "description": "Annotation Service"
}
 */
