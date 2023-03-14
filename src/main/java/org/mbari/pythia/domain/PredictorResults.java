package org.mbari.pythia.domain;

import java.util.List;

/**
 * Class to match the output of https://github.com/mbari-org/keras-model-server-fast-api
 * @param success
 * @param predictions
 */
public record PredictorResults(boolean success, List<Prediction> predictions) {

    public static PredictorResults from(PredictResults predictResults) {
        var predictions = predictResults.boundingBoxes()
                .stream()
                .map(Prediction::from)
                .toList();
        return new PredictorResults(true, predictions);
    }
}
