package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import com.swirlfist.sprited.domain.error.RedoEditorActionError
import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import javax.inject.Inject

class RedoEditorActionUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : RedoEditorActionUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<PixelImageEditorActionContext> {
        val redoResult = pixelImageEditorActionRepository.redoAction()
        return if (redoResult == null) {
            Result.failure(RedoEditorActionError())
        } else {
            Result.success(redoResult)
        }
    }
}