package com.swirlfist.sprited.presentation.state

data class PaletteEditDialogState(
    val paletteEditState: PaletteEditState = PaletteEditState(),
    val pixelImagePreviewSectionState: PixelImagePreviewSectionState? = null,
)