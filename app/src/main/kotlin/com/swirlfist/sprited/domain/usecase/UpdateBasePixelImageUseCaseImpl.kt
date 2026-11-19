package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.BasePixelImageRepository
import javax.inject.Inject

class UpdateBasePixelImageUseCaseImpl @Inject constructor(
    private val basePixelImageRepository: BasePixelImageRepository,
) : UpdateBasePixelImageUseCase {
    override suspend fun invoke(params: UpdateBasePixelImageUseCase.Params): Result<Unit> {
        basePixelImageRepository.updateBasePixelImage(params.pixelImage)

        return Result.success(Unit)
    }
}
