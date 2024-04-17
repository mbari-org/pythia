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

import java.util.concurrent.CompletionStage;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.mbari.pythia.domain.PredictorResults;
import org.mbari.pythia.services.YoloEventQueue;

/**
 * Endpoints for running a prediction on an image. The output is compatible with CVisionAI's
 * endpoints.
 */
@Path("/predictor")
public class PredictorResource {

    @Inject
    YoloEventQueue yolov5;

    // https://quarkus.io/guides/context-propagation
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor managedExecutor;

    /**
     * Prediction results that match the output of Class to match the output of https://github.com/mbari-org/keras-model-server-fast-api
     * @param file
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<PredictorResults> predict(@RestForm("file") FileUpload file) {
        // https://quarkus.io/guides/resteasy-reactive#handling-multipart-form-data
        var imagePath = file.uploadedFile();

        return threadContext.withContextCapture(
                yolov5.predict(imagePath).thenApplyAsync(PredictorResults::from, managedExecutor));
    }
}
