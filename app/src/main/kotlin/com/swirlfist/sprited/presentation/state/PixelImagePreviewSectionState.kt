package com.swirlfist.sprited.presentation.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong

data class PixelImagePreviewSectionState(
    val isFitAvailableSpace: Boolean = false,
    val selectedPreviewBackgroundColorLong: Long = Color.White.toColorLong(),
    val onPreviewBackgroundColorSelected: (Color) -> Unit,
)

fun PixelImagePreviewSectionState.updateSelectedPreviewBackgroundColor(color: Color) = copy(
    selectedPreviewBackgroundColorLong = color.toColorLong(),
)
