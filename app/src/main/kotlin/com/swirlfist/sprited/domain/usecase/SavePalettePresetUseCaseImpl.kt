package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PalettePresetsRepository
import com.swirlfist.sprited.domain.error.SavePalettePresetError
import java.io.IOException
import javax.inject.Inject

class SavePalettePresetUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : SavePalettePresetUseCase {
    override suspend fun invoke(params: SavePalettePresetUseCase.Params): Result<Unit> = try {
        palettePresetsRepository.addPalettePreset(params.preset)
        Result.success(Unit)
    } catch (e: IOException) {
        Result.failure(SavePalettePresetError(e))
    }
}
