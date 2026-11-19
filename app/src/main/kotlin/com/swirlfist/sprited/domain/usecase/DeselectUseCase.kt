package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelSelectionModel

interface DeselectUseCase : UseCase<DeselectUseCase.Params, PixelSelectionModel?> {

    data class Params(val actionContext: PixelImageEditorActionContext) : UseCaseParams
}
