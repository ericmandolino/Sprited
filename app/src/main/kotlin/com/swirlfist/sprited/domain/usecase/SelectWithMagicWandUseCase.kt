package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelSelectionModel

interface SelectWithMagicWandUseCase :
    UseCase<SelectWithMagicWandUseCase.Params, PixelSelectionModel> {

    data class Params(val actionContext: PixelImageEditorActionContext, val x: Int, val y: Int) :
        UseCaseParams
}
