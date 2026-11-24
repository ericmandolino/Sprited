package com.swirlfist.sprited.domain.usecase

import android.net.Uri
import com.swirlfist.sprited.data.repository.BasePixelImageRepository
import com.swirlfist.sprited.domain.error.SavePixelImageError
import com.swirlfist.sprited.domain.error.WriteToFileError
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.presentation.mapper.toPixelImageSaveModel
import javax.inject.Inject
import kotlinx.serialization.json.Json

class SavePixelImageUseCaseImpl @Inject constructor(
    private val writeToFileUseCase: WriteToFileUseCase,
    private val basePixelImageRepository: BasePixelImageRepository,
) : SavePixelImageUseCase {
    override suspend fun invoke(params: SavePixelImageUseCase.Params): Result<Unit> = try {
        savePixelImage(
            params.pixelImageModel,
            params.uri,
        )
        Result.success(Unit)
    } catch (e: WriteToFileError) {
        Result.failure(SavePixelImageError(e))
    }

    private suspend fun savePixelImage(pixelImageModel: PixelImageModel, uri: Uri) {
        val content = Json.encodeToString(pixelImageModel.toPixelImageSaveModel())

        writeToFileUseCase(
            WriteToFileUseCase.Params(
                content,
                uri,
            ),
        ).getOrThrow()

        basePixelImageRepository.updateBasePixelImage(pixelImageModel)
    }
}
