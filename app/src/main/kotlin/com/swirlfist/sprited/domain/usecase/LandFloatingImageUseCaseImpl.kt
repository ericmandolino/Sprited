package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import com.swirlfist.sprited.domain.getPixelAt
import com.swirlfist.sprited.domain.getPixelHeight
import com.swirlfist.sprited.domain.getPixelWidth
import com.swirlfist.sprited.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.sprited.domain.model.PixelImageEditorAction
import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import javax.inject.Inject

class LandFloatingImageUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : LandFloatingImageUseCase {
    override suspend fun invoke(
        params: LandFloatingImageUseCase.Params,
    ): Result<PixelImageEditorActionContext> = Result.success(
        landFloatingImage(
            actionContext = params.actionContext,
        ),
    )

    private suspend fun landFloatingImage(
        actionContext: PixelImageEditorActionContext,
    ): PixelImageEditorActionContext {
        val pixelImage = actionContext.pixelImage
        val pixelMatrix = pixelImage.pixelMatrixModel
        val floatingPixelImage = actionContext.floatingPixelImage

        val imageContentResult = if (floatingPixelImage == null) {
            pixelMatrix.content
        } else {
            val offsetX = floatingPixelImage.offset.first
            val offsetY = floatingPixelImage.offset.second
            val floatingImageWidth = floatingPixelImage.pixelImageModel.getPixelWidth()
            val floatingImageHeight = floatingPixelImage.pixelImageModel.getPixelHeight()

            pixelMatrix.content.mapIndexed { y, row ->
                val floatingY = y - offsetY
                row.mapIndexed { x, pixel ->
                    val floatingX = x - offsetX
                    when {
                        floatingX in (0..<floatingImageWidth) &&
                            floatingY in (0..<floatingImageHeight) -> {
                            val floatingPixel = floatingPixelImage.pixelImageModel.getPixelAt(
                                floatingX,
                                floatingY,
                            )
                            if (floatingPixel != null &&
                                floatingPixel.paletteIndex != EMPTY_PIXEL_PALETTE_INDEX
                            ) {
                                floatingPixel
                            } else {
                                pixel
                            }
                        }
                        else -> pixel
                    }
                }
            }
        }

        val pixelImageResult = pixelImage.copy(
            pixelMatrixModel = pixelImage.pixelMatrixModel.copy(
                content = imageContentResult,
            ),
        )

        val actionContextResult = actionContext.copy(
            pixelImage = pixelImageResult,
            floatingPixelImage = null,
        )

        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.LandFloatingImageAction(
                actionContext,
            ),
            actionContextResult,
        )

        return actionContextResult
    }
}
