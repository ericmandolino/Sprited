package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelImageModel

interface MoveImageUseCase : UseCase<MoveImageUseCase.Params, PixelImageModel> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
        val moveDirection: MoveDirection,
    ) : UseCaseParams
}

enum class MoveDirection { UP, DOWN, LEFT, RIGHT }
