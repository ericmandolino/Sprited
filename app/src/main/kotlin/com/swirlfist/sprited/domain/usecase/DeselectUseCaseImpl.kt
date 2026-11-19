package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import com.swirlfist.sprited.domain.model.PixelImageEditorAction
import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelSelectionModel
import javax.inject.Inject

class DeselectUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : DeselectUseCase {

    override suspend fun invoke(params: DeselectUseCase.Params): Result<PixelSelectionModel?> {
        deselect(
            actionContext = params.actionContext,
        )

        return Result.success(null)
    }

    private suspend fun deselect(actionContext: PixelImageEditorActionContext) {
        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.DeselectAction(
                actionContext,
            ),
            actionContextResult = actionContext.copy(
                pixelSelection = null,
                floatingPixelImage = null,
            ),
        )
    }
}
