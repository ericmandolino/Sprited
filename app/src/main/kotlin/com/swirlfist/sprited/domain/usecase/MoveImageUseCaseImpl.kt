package com.swirlfist.sprited.domain.usecase

import com.swirlfist.sprited.data.repository.PixelImageEditorActionRepository
import com.swirlfist.sprited.domain.isBlank
import com.swirlfist.sprited.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.sprited.domain.model.PixelImageEditorAction
import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelMatrixModel
import com.swirlfist.sprited.domain.model.PixelModel
import javax.inject.Inject

class MoveImageUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : MoveImageUseCase {
    override suspend fun invoke(params: MoveImageUseCase.Params): Result<PixelImageModel> =
        Result.success(
            moveImage(
                actionContext = params.actionContext,
                moveDirection = params.moveDirection,
            ),
        )

    private suspend fun moveImage(
        actionContext: PixelImageEditorActionContext,
        moveDirection: MoveDirection,
    ): PixelImageModel {
        val pixelImage = actionContext.pixelImage

        if (pixelImage.pixelMatrixModel.isBlank()) {
            return pixelImage
        }

        val pixelImageResult = when (moveDirection) {
            MoveDirection.UP -> pixelImage.moveImageUp()
            MoveDirection.DOWN -> pixelImage.moveImageDown()
            MoveDirection.LEFT -> pixelImage.moveImageLeft()
            MoveDirection.RIGHT -> pixelImage.moveImageRight()
        }
        val actionContextResult = actionContext.copy(
            pixelImage = pixelImageResult,
        )

        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.MoveImageAction(
                actionContext,
                moveDirection,
            ),
            actionContextResult,
        )

        return pixelImageResult
    }

    private fun PixelImageModel.moveImageUp(): PixelImageModel = copy(
        pixelMatrixModel = pixelMatrixModel.moveUp(),
    )

    private fun PixelImageModel.moveImageDown(): PixelImageModel = copy(
        pixelMatrixModel = pixelMatrixModel.moveDown(),
    )

    private fun PixelImageModel.moveImageLeft(): PixelImageModel = copy(
        pixelMatrixModel = pixelMatrixModel.moveLeft(),
    )

    private fun PixelImageModel.moveImageRight(): PixelImageModel = copy(
        pixelMatrixModel = pixelMatrixModel.moveRight(),
    )

    private fun PixelMatrixModel.moveUp(): PixelMatrixModel {
        val matrix = content
        val size = matrix.size
        val newRow = mutableListOf<PixelModel>().apply {
            repeat(size) {
                add(PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX))
            }
        }

        return copy(
            content = listOf(newRow) + matrix.dropLast(1),
        )
    }

    private fun PixelMatrixModel.moveDown(): PixelMatrixModel {
        val matrix = content
        val size = matrix.size
        val newRow = mutableListOf<PixelModel>().apply {
            repeat(size) {
                add(PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX))
            }
        }

        return copy(
            content = matrix.drop(1).plusElement(newRow),
        )
    }

    private fun PixelMatrixModel.moveLeft(): PixelMatrixModel {
        val newMatrix = mutableListOf<List<PixelModel>>()
        val emptyPixel = PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX)
        content.forEach { row ->
            newMatrix.add(row.drop(1).plus(emptyPixel))
        }

        return copy(
            content = newMatrix,
        )
    }

    private fun PixelMatrixModel.moveRight(): PixelMatrixModel {
        val newMatrix = mutableListOf<List<PixelModel>>()
        val emptyPixel = PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX)
        content.forEach { row ->
            newMatrix.add(listOf(emptyPixel) + row.dropLast(1))
        }

        return copy(
            content = newMatrix,
        )
    }
}
