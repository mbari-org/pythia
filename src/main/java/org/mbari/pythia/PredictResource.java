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

import java.util.List;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.mbari.pythia.domain.BoundingBox;
import org.mbari.pythia.domain.PredictResults;
import org.mbari.pythia.services.Yolov5EventQueue;

@Path("/predict")
public class PredictResource {

    @Inject
    Yolov5EventQueue yolov5;

    // https://quarkus.io/guides/context-propagation
    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor managedExecutor;

    /**
     * Runs inference on an image using Yolov5 and the model provided to the server
     * @param file The image (as a multipart form)
     * @return Any inferred predictions found in the image.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<BoundingBox>> predict(@RestForm("file") FileUpload file) {
        // https://quarkus.io/guides/resteasy-reactive#handling-multipart-form-data
        var imagePath = file.uploadedFile();

        return threadContext.withContextCapture(
                yolov5.predict(imagePath).thenApplyAsync(PredictResults::boundingBoxes, managedExecutor));
    }
}
