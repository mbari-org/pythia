package org.mbari.pythia.domain;

import java.util.List;

/**
 * Class to match the output of https://github.com/mbari-org/keras-model-server-fast-api
 * @param category_id
 * @param scores
 * @param bbox
 */
public record Prediction(String category_id,
                         List<Double> scores,
                         List<Double> bbox) {

    public static Prediction from(BoundingBox box) {
        return new Prediction(box.concept(),
                List.of(box.probability()),
                asBbox(box));
    }

    private static List<Double> asBbox(BoundingBox b) {
        return List.of(b.x(), b.y(),
                b.x() + b.width(), b.y() + b.height());
    }
}
