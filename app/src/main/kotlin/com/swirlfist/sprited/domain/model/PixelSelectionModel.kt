package com.swirlfist.sprited.domain.model

data class PixelSelectionModel(
    val selected: List<List<PixelModel?>>,
    val rectangleSelection: RectangleSelectionModel? = null,
)
