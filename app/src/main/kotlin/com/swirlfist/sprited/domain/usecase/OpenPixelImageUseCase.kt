package com.swirlfist.sprited.domain.usecase

import android.net.Uri
import com.swirlfist.sprited.domain.model.PixelImageModel

interface OpenPixelImageUseCase : UseCase<OpenPixelImageUseCase.Params, PixelImageModel> {

    data class Params(val uri: Uri) : UseCaseParams
}
