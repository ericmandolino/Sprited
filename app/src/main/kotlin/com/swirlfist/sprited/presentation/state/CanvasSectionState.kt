package com.swirlfist.sprited.presentation.state

import com.swirlfist.sprited.domain.model.FloatingPixelImageModel
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelSelectionModel
import com.swirlfist.sprited.domain.usecase.RectangleSelectionActionType

data class CanvasSectionState(
    val pixelImageModel: PixelImageModel? = null,
    val pixelSelectionModel: PixelSelectionModel? = null,
    val floatingPixelImageModel: FloatingPixelImageModel? = null,
    val zoomFactor: Float = 1F,
    val isShowGridEnabled: Boolean = true,
    val rectangleSelectionActionType: RectangleSelectionActionType? = null,
    val isVisitingPixels: Boolean = false,
    val lastVisitedPixel: Pair<Int, Int>? = null,
)