package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import javax.inject.Inject

class ClearEditorActionsUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : ClearEditorActionsUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<Unit> {
        pixelImageEditorActionRepository.clearActions()

        return Result.success(Unit)
    }
}