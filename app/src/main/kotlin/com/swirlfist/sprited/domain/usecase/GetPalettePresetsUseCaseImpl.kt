package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PalettePresetsRepository
import com.swirlfist.sprited.domain.error.LoadPalettePresetsError
import com.swirlfist.sprited.domain.model.PalettePresetModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPalettePresetsUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : GetPalettePresetsUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<Flow<List<PalettePresetModel>>> {
        return try {
            Result.success(palettePresetsRepository.getPalettePresets())
        } catch (e: Exception) {
            Result.failure(LoadPalettePresetsError(e))
        }
    }
}