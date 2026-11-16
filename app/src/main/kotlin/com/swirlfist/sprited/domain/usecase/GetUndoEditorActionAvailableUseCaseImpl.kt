package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUndoEditorActionAvailableUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : GetUndoEditorActionAvailableUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<Flow<Boolean>> {
        return Result.success(pixelImageEditorActionRepository.isUndoAvailable())
    }
}