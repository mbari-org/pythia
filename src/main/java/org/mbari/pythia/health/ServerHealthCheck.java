package org.mbari.pythia.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.mbari.pythia.AppConfig;

import javax.enterprise.context.ApplicationScoped;

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

