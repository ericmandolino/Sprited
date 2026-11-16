package com.swirlfist.sprited.data.repository

import com.swirlfist.sprited.domain.model.PixelImageModel
import javax.inject.Inject

class BasePixelImageRepositoryImpl @Inject constructor() : BasePixelImageRepository {
    private var _basePixelImage: PixelImageModel? = null

    override suspend fun getBasePixelImage(): PixelImageModel? {
        return _basePixelImage
    }

    override suspend fun updateBasePixelImage(pixelImage: PixelImageModel) {
        _basePixelImage = pixelImage
    }
}