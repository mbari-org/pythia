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
package org.mbari.pythia.domain;

import java.util.List;

/**
 * Class to match the output of https://github.com/mbari-org/keras-model-server-fast-api
 * @param success
 * @param predictions
 */
public record PredictorResults(boolean success, List<Prediction> predictions) {

    public static PredictorResults from(PredictResults predictResults) {
        var predictions =
                predictResults.boundingBoxes().stream().map(Prediction::from).toList();
        return new PredictorResults(true, predictions);
    }
}
