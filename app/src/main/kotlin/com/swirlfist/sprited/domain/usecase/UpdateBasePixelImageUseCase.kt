package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.domain.model.PixelImageModel

interface UpdateBasePixelImageUseCase : UseCase<UpdateBasePixelImageUseCase.Params, Unit> {

    data class Params(val pixelImage: PixelImageModel) : UseCaseParams
}
