package org.mbari.pythia.domain;

import java.util.List;

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
