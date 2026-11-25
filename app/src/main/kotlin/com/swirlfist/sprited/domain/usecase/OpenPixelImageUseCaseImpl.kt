package com.swirlfist.sprited.domain.usecase

import android.net.Uri
import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import com.swirlfist.sprited.domain.error.OpenPixelImageError
import com.swirlfist.sprited.domain.error.ReadFromFileError
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.presentation.mapper.toPixelImageModel
import com.swirlfist.sprited.presentation.model.PixelImageSaveModel
import javax.inject.Inject
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class OpenPixelImageUseCaseImpl @Inject constructor(
    private val readFromFileUseCase: ReadFromFileUseCase,
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : OpenPixelImageUseCase {
    override suspend fun invoke(params: OpenPixelImageUseCase.Params): Result<PixelImageModel> =
        try {
            val pixelImage = openPixelImage(params.uri)
            pixelImageEditorActionRepository.clearActions()
            Result.success(pixelImage)
        } catch (e: ReadFromFileError) {
            Result.failure(OpenPixelImageError(e))
        } catch (e: SerializationException) {
            Result.failure(OpenPixelImageError(e))
        } catch (e: IllegalArgumentException) {
            Result.failure(OpenPixelImageError(e))
        }

    private suspend fun openPixelImage(uri: Uri): PixelImageModel = readFromFileUseCase(
        params = ReadFromFileUseCase.Params(
            uri,
        ),
    ).getOrThrow().let { content ->
        Json.decodeFromString<PixelImageSaveModel>(content).toPixelImageModel()
    }
}
