package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PalettePresetModel
import kotlinx.coroutines.flow.Flow

interface GetPalettePresetsUseCase : UseCase<UseCaseParams.NoParams, Flow<List<PalettePresetModel>>>