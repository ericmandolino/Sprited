package com.swirlfist.sprited.domain.model

import androidx.annotation.ColorLong
import kotlinx.serialization.Serializable

@Serializable
data class PaletteModel(
    @ColorLong val colors: List<Long>
)