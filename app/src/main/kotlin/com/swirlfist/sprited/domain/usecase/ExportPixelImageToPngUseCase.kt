package com.swirlfist.sprited.domain.usecase

import android.net.Uri
import com.swirlfist.sprited.domain.model.PixelImageModel

interface ExportPixelImageToPngUseCase : UseCase<ExportPixelImageToPngUseCase.Params, Unit> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val uri: Uri,
        val scale: Float = 1F,
    ) : UseCaseParams
}
