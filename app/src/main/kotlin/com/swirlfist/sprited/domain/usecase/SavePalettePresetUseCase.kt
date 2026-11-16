package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PalettePresetModel

interface SavePalettePresetUseCase : UseCase<SavePalettePresetUseCase.Params, Unit> {

    data class Params(
        val preset: PalettePresetModel,
    ) : UseCaseParams
}