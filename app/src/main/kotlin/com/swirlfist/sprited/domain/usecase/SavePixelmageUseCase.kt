package com.swirlfist.sprited.domain.usecase

import android.net.Uri
import com.swirlfist.sprited.domain.model.PixelImageModel

interface SavePixelImageUseCase : UseCase<SavePixelImageUseCase.Params, Unit> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val uri: Uri,
    ) : UseCaseParams
}