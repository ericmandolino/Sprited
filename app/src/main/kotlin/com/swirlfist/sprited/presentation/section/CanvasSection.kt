package com.swirlfist.sprited.presentation.section

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import com.swirlfist.sprited.domain.createSelection
import com.swirlfist.sprited.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.sprited.domain.model.FloatingPixelImageModel
import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelMatrixModel
import com.swirlfist.sprited.domain.model.PixelModel
import com.swirlfist.sprited.domain.model.RectangleSelectionModel
import com.swirlfist.sprited.presentation.state.CanvasSectionState
import com.swirlfist.sprited.presentation.theme.SpritedTheme
import com.swirlfist.sprited.presentation.uielements.PixelCanvas
import com.swirlfist.sprited.presentation.uielements.createCheckersPixelImage

@Composable
fun CanvasSection(
    modifier: Modifier = Modifier,
    state: CanvasSectionState,
    onEvent: (CanvasSectionEvent) -> Unit,
) {
    val pixelImage = state.pixelImageModel

    if (pixelImage != null) {
        PixelCanvas(
            modifier = modifier,
            pixelImage = pixelImage,
            pixelSelection = state.pixelSelectionModel,
            floatingPixelImage = state.floatingPixelImageModel,
            initialZoomFactor = state.zoomFactor,
            isShowGridEnabled = state.isShowGridEnabled,
            onPixelVisited = { x, y -> onEvent(CanvasSectionEvent.PixelVisited(x, y)) },
            onPixelVisitCancel = { onEvent(CanvasSectionEvent.PixelVisitCancel) },
            onPixelVisitFinish = { onEvent(CanvasSectionEvent.PixelVisitEnd) },
            onZoomUpdate = { zoom -> onEvent(CanvasSectionEvent.ZoomUpdate(zoom)) },
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionPreview() {
    SpritedTheme {
        val pixelImage = createCheckersPixelImage(
            width = 5,
            height = 4,
            color1 = Color.Black.toColorLong(),
            color2 = Color.Yellow.toColorLong(),
        )
        val pixelSelection = pixelImage.createSelection(
            selectedCoordinates = listOf(
                Pair(1, 2),
                Pair(2, 2),
                Pair(1, 1),
                Pair(2, 3),
                Pair(0, 1),
            )
        ).copy(
            rectangleSelection = RectangleSelectionModel(
                start = Pair(3, 2),
                end = Pair(4, 4),
            )
        )

        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = pixelImage,
                pixelSelectionModel = pixelSelection,
                zoomFactor = 1F,
                isShowGridEnabled = false,
            )
        ) { event ->
            Log.d("CanvasSection", "event: $event")
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionZoomedPreview() {
    SpritedTheme {
        val pixelImage = createCheckersPixelImage(
            width = 5,
            height = 4,
            color1 = Color.Black.toColorLong(),
            color2 = Color.Yellow.toColorLong(),
        )
        val pixelSelection = pixelImage.createSelection(
            selectedCoordinates = listOf(
                Pair(1, 2),
                Pair(2, 2),
                Pair(1, 1),
                Pair(2, 3),
                Pair(0, 1),
            )
        ).copy(
            rectangleSelection = RectangleSelectionModel(
                start = Pair(2, 2),
                end = Pair(3, 4),
            )
        )

        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = pixelImage,
                pixelSelectionModel = pixelSelection,
                zoomFactor = 4F,
                isShowGridEnabled = true,
            )
        ) { event ->
            Log.d("CanvasSection", "event: $event")
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionEmptyImagePreview() {
    SpritedTheme {
        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = PixelImageModel.createEmpty(
                    width = 4,
                    height = 4,
                    colors = listOf(Color.Black.toColorLong(), Color.Yellow.toColorLong()),
                ),
                zoomFactor = 1F,
            )
        ) { event ->
            Log.d("CanvasSection", "event: $event")
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionFloatingImagePreview() {
    SpritedTheme {
        val pixelImage = createCheckersPixelImage(
            width = 5,
            height = 4,
            color1 = Color.Black.toColorLong(),
            color2 = Color.Yellow.toColorLong(),
        )

        val floatingPixelImage = FloatingPixelImageModel(
            pixelImageModel = PixelImageModel(
                pixelMatrixModel = PixelMatrixModel(
                    content = listOf(
                        listOf(
                            PixelModel(0),
                            PixelModel(0),
                            PixelModel(0),
                        ),
                        listOf(
                            PixelModel(EMPTY_PIXEL_PALETTE_INDEX),
                            PixelModel(EMPTY_PIXEL_PALETTE_INDEX),
                            PixelModel(0),
                        ),
                        listOf(
                            PixelModel(EMPTY_PIXEL_PALETTE_INDEX),
                            PixelModel(EMPTY_PIXEL_PALETTE_INDEX),
                            PixelModel(0),
                        ),
                    )
                ),
                paletteModel = PaletteModel(
                    colors = listOf(Color.White.toColorLong()),
                )
            ),
            offset = Pair(-1, 1),
        )

        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = pixelImage,
                floatingPixelImageModel = floatingPixelImage,
                zoomFactor = 1F,
                isShowGridEnabled = true,
            )
        ) { event ->
            Log.d("CanvasSection", "event: $event")
        }
    }
}