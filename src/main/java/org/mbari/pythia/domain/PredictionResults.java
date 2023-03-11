package org.mbari.pythia.domain;

import ai.djl.modality.cv.Image;

import java.util.List;

public record PredictionResults(Image image, List<BoundingBox> boundingBoxes) {}

