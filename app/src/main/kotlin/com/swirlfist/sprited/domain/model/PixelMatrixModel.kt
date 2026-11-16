package com.swirlfist.sprited.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PixelMatrixModel(
    val content: List<List<PixelModel>>,
)