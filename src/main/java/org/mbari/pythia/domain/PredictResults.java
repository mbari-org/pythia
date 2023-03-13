package org.mbari.pythia.domain;

import ai.djl.modality.cv.Image;

import java.util.List;

public record PredictResults(Image image, List<BoundingBox> boundingBoxes) {}

