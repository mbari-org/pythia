package org.mbari.pythia;

import org.mbari.pythia.services.Yolov5EventQueue;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Produces;
import java.nio.file.Path;

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
