package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import com.swirlfist.sprited.domain.error.UndoEditorActionError
import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import javax.inject.Inject

class UndoEditorActionUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : UndoEditorActionUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<PixelImageEditorActionContext> {
        val undoResult = pixelImageEditorActionRepository.undoAction()
        return if (undoResult == null) {
            Result.failure(UndoEditorActionError())
        } else {
            Result.success(undoResult)
        }
    }

}