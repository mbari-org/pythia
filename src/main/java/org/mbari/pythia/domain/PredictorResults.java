package org.mbari.pythia.domain;

import java.util.List;

public record PredictorResults(boolean success, List<Prediction> predictions) {

    public static PredictorResults from(PredictResults predictResults) {
        var predictions = predictResults.boundingBoxes()
                .stream()
                .map(Prediction::from)
                .toList();
        return new PredictorResults(true, predictions);
    }
}
