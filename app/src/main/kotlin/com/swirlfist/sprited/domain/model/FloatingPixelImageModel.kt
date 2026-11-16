package com.swirlfist.sprited.domain.model

data class FloatingPixelImageModel(
    val pixelImageModel: PixelImageModel,
    val offset: Pair<Int, Int> = Pair(0, 0),
)
