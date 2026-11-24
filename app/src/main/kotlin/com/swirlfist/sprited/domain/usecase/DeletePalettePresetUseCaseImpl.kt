package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PalettePresetsRepository
import com.swirlfist.sprited.domain.error.DeletePalettePresetError
import java.io.IOException
import javax.inject.Inject

class DeletePalettePresetUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : DeletePalettePresetUseCase {
    override suspend fun invoke(params: DeletePalettePresetUseCase.Params): Result<Unit> = try {
        palettePresetsRepository.deletePalettePreset(params.preset)
        Result.success(Unit)
    } catch (e: IOException) {
        Result.failure(DeletePalettePresetError(e))
    }
}
