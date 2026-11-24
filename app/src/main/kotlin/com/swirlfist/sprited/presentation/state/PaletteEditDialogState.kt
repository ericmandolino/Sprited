package com.swirlfist.sprited.presentation.state

import com.swirlfist.sprited.domain.model.PixelImageModel

data class PaletteEditDialogState(
    val paletteEditState: PaletteEditState = PaletteEditState(),
    val pixelImagePreviewSectionState: PixelImagePreviewSectionState? = null,
    val pixelImageModel: PixelImageModel? = null,
)
