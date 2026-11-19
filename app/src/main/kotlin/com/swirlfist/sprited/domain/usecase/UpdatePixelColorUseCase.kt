package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelImageModel

interface UpdatePixelColorUseCase : UseCase<UpdatePixelColorUseCase.Params, PixelImageModel> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
        val isSameAction: Boolean,
    ) : UseCaseParams
}
