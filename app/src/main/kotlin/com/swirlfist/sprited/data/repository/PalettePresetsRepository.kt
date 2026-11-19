package com.swirlfist.sprited.data.repository

import com.swirlfist.sprited.domain.model.PalettePresetModel
import kotlinx.coroutines.flow.Flow

interface PalettePresetsRepository {
    suspend fun addPalettePreset(preset: PalettePresetModel)
    suspend fun deletePalettePreset(preset: PalettePresetModel)
    suspend fun getPalettePresets(): Flow<List<PalettePresetModel>>
}
