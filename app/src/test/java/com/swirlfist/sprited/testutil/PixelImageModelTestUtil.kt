package com.swirlfist.sprited.testutil

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelMatrixModel
import com.swirlfist.sprited.domain.model.PixelModel
import kotlin.math.max

class PixelImageModelTestUtil {

    companion object {

        val paletteColors = listOf(
            Color.White.toColorLong(),
            Color.Black.toColorLong(),
            Color.Red.toColorLong(),
            Color.Green.toColorLong(),
            Color.Blue.toColorLong(),
            Color.Yellow.toColorLong(),
        )

        fun createPixelImageModel(
            pixelImageString: String,
            paletteSize: Int = 0,
        ): PixelImageModel {
            var maxPaletteIndex = 0
            val rows = mutableListOf<List<PixelModel>>()

            val stringRows = pixelImageString.split("\n")
            stringRows.forEach { stringRow ->
                val stringPixels = stringRow.split(" ")
                val pixelModelRow = mutableListOf<PixelModel>()
                stringPixels.forEach { stringPixel ->
                    val paletteIndex = stringPixel.toInt()
                    pixelModelRow.add(PixelModel(paletteIndex))
                    maxPaletteIndex = max(maxPaletteIndex, paletteIndex)
                }

                rows.add(pixelModelRow.toList())
            }

            return PixelImageModel(
                pixelMatrixModel = PixelMatrixModel(
                    content = rows.toList()
                ),
                paletteModel = PaletteModel(
                    colors = paletteColors.subList(
                        0,
                        if (paletteSize > 0) paletteSize else maxPaletteIndex + 1
                    )
                )
            )
        }
    }
}