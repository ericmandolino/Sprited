package com.swirlfist.sprited.data.repository

import com.swirlfist.sprited.domain.model.PixelImageModel

interface BasePixelImageRepository {
    suspend fun getBasePixelImage(): PixelImageModel?
    suspend fun updateBasePixelImage(pixelImage: PixelImageModel)
}
