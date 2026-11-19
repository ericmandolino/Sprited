package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetRedoEditorActionAvailableUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : GetRedoEditorActionAvailableUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<Flow<Boolean>> =
        Result.success(pixelImageEditorActionRepository.isRedoAvailable())
}
