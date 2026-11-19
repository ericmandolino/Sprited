package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext

interface LandFloatingImageUseCase :
    UseCase<LandFloatingImageUseCase.Params, PixelImageEditorActionContext> {
    data class Params(val actionContext: PixelImageEditorActionContext) : UseCaseParams
}
