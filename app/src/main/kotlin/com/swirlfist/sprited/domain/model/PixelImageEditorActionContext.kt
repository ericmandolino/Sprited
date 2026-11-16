package com.swirlfist.sprited.domain.model

data class PixelImageEditorActionContext(
    val pixelImage: PixelImageModel,
    val pixelSelection: PixelSelectionModel? = null,
    val floatingPixelImage: FloatingPixelImageModel? = null,
)
