package com.swirlfist.sprited.presentation.state

import com.swirlfist.sprited.domain.model.PalettePresetModel

data class PalettePresetsDialogState(
    val palettePresets: List<PalettePresetModel> = listOf(),
    val isLoadingPresets: Boolean = true,
)
