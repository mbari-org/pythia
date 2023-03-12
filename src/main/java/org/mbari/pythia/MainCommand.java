package org.mbari.pythia;

import io.quarkus.runtime.Quarkus;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command
public class MainCommand implements Runnable {

    @CommandLine.Parameters(index = "0",
            description = {"Path to the torchscript model"})
    Path modelPath;

    @CommandLine.Parameters(index = "1",
            description = "Path to the names file")
    Path namesPath;

    @Override
    public void run() {
        // https://github.com/quarkusio/quarkus/issues/24204
        Quarkus.waitForExit(); // MUST HAVE THIS
    }
}
