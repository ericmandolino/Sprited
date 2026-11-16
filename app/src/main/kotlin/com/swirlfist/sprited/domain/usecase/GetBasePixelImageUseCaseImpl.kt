package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.BasePixelImageRepository
import com.swirlfist.sprited.domain.model.PixelImageModel
import javax.inject.Inject

class GetBasePixelImageUseCaseImpl @Inject constructor(
    private val basePixelImageRepository: BasePixelImageRepository
) : GetBasePixelImageUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<PixelImageModel?> {
        return Result.success(basePixelImageRepository.getBasePixelImage())
    }
}