package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PalettePresetsRepository
import com.swirlfist.sprited.domain.error.LoadPalettePresetsError
import com.swirlfist.sprited.domain.model.PalettePresetModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetPalettePresetsUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : GetPalettePresetsUseCase {
    override suspend fun invoke(
        params: UseCaseParams.NoParams,
    ): Result<Flow<List<PalettePresetModel>>> = try {
        Result.success(palettePresetsRepository.getPalettePresets())
    } catch (e: IOException) {
        Result.failure(LoadPalettePresetsError(e))
    }
}
