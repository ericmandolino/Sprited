package com.swirlfist.sprited.presentation.mapper

import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelMatrixModel
import com.swirlfist.sprited.domain.model.PixelModel
import com.swirlfist.sprited.presentation.model.PixelImageSaveModel

fun PixelImageModel.toPixelImageSaveModel(): PixelImageSaveModel {
    return PixelImageSaveModel(
        pixels = pixelMatrixModel.content.map { row ->
            row.map { pixel ->
                pixel.paletteIndex
            }
        },
        palette = paletteModel.colors,
    )
}

fun PixelImageSaveModel.toPixelImageModel(): PixelImageModel {
    return PixelImageModel(
        pixelMatrixModel = PixelMatrixModel(
            content = pixels.map { row ->
                row.map { pixelPaletteIndex ->
                    PixelModel(pixelPaletteIndex)
                }
            },
        ),
        paletteModel = PaletteModel(palette),
    )
}