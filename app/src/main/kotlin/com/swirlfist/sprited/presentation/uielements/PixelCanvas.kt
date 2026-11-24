package com.swirlfist.sprited.presentation.uielements

import android.util.SizeF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.swirlfist.sprited.domain.getPixelHeight
import com.swirlfist.sprited.domain.getPixelWidth
import com.swirlfist.sprited.domain.model.FloatingPixelImageModel
import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelMatrixModel
import com.swirlfist.sprited.domain.model.PixelModel
import com.swirlfist.sprited.domain.model.PixelSelectionModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val NO_ZOOM_FACTOR = 1F
private const val PIXEL_SIZE_DP_CANVAS = 32
private const val PIXEL_SIZE_DP_PREVIEW = 1
private const val PAN_VS_ZOOM_FACTOR = 0.025F
private const val ZOOM_STEP = 0.025F
const val MAX_ZOOM_FACTOR = 2F
const val MIN_ZOOM_FACTOR = 0.25F

@Suppress("LongMethod")
@Composable
fun PixelCanvas(modifier: Modifier, state: PixelCanvasState) {
    val offsetSaver = createOffsetSaver()
    val intSizeSaver = createIntSizeSaver()
    val imageOffset = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset.Zero) }
    val margin = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset.Zero) }
    val lastCanvasSize =
        rememberSaveable(stateSaver = intSizeSaver) { mutableStateOf(IntSize(-1, -1)) }
    val initialZoomFactor = state.initialZoomFactor
    var zoomFactor by rememberSaveable { mutableFloatStateOf(initialZoomFactor) }
    var zoomAcc by rememberSaveable { mutableFloatStateOf(0F) }
    var lastPixelVisited by rememberSaveable { mutableStateOf<Pair<Int, Int>?>(null) }

    val pixelImage = state.pixelImage
    val imagePixelSize = IntSize(pixelImage.getPixelWidth(), pixelImage.getPixelHeight())

    LaunchedEffect(initialZoomFactor) {
        zoomFactor = initialZoomFactor
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
                onCanvasSizeChanged(size, lastCanvasSize, imageOffset)
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        if (event.changes.size != 1) {
                            if (lastPixelVisited != null) {
                                lastPixelVisited = null
                                state.onPixelVisitCancel()
                            }

                            do {
                                val eventToConsume = awaitPointerEvent()
                            } while (eventToConsume.changes.any { it.pressed })

                            break
                        }

                        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_CANVAS, zoomFactor)

                        val pixelVisited = getPixelCoordinatesAt(
                            offset = event.changes[0].position,
                            CanvasContext(
                                pixelSizeInt,
                                imagePixelSize,
                                canvasSize = IntSize(
                                    lastCanvasSize.value.width,
                                    lastCanvasSize.value.height,
                                ),
                                imageOffset.value,
                            ),
                            margin.value,
                        )

                        if (pixelVisited != null && pixelVisited != lastPixelVisited) {
                            lastPixelVisited = pixelVisited
                            state.onPixelVisited(
                                pixelVisited.first,
                                pixelVisited.second,
                            )
                        }

                        event.changes.forEach { pointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    lastPixelVisited = null
                    state.onPixelVisitFinish()
                }
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        if (event.changes.size != 2) {
                            zoomAcc = 0F
                            continue
                        }

                        val pan = event.calculatePan()
                        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_CANVAS, zoomFactor)
                        val canvasContext = CanvasContext(
                            pixelSizeInt,
                            imagePixelSize,
                            canvasSize = size,
                            imageOffset.value,
                        )
                        val updateImageOffset: (Offset) -> Unit = { newOffset ->
                            imageOffset.value = newOffset
                        }

                        onCanvasPan(
                            pan,
                            canvasContext,
                            updateImageOffset,
                        )

                        val zoomDistance = (event.changes[0].position - event.changes[1].position)
                            .getDistance()
                        if (pan.getDistance() < zoomDistance * PAN_VS_ZOOM_FACTOR) {
                            val updatedZoom = onCanvasZoom(
                                ZoomContext(
                                    Zoom(zoomAcc, zoomFactor),
                                    zoomDelta = event.calculateZoom(),
                                    zoomCentroid = event.calculateCentroid(),
                                ),
                                canvasContext,
                                state.onZoomUpdate,
                                updateImageOffset,
                            )
                            zoomAcc = updatedZoom.acc
                            zoomFactor = updatedZoom.factor
                        }

                        event.changes.forEach { pointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
    ) {
        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_CANVAS, zoomFactor)
        val canvasSize = size
        val imageSize = Size(
            width = (imagePixelSize.width * pixelSizeInt).toFloat(),
            height = (imagePixelSize.height * pixelSizeInt).toFloat(),
        )

        adjustImageOffset(imageOffset, margin, canvasSize, imageSize)

        drawCanvas(
            state = CanvasDrawState(
                drawImageState = CanvasDrawImageState(
                    pixelImage,
                    state.pixelSelection,
                    pixelSizeInt,
                    imageOffset.value,
                    imagePixelSize,
                    imageSize,
                    margin.value,
                ),
                state.floatingPixelImage,
                isShowBorderEnabled = false,
                state.isShowGridEnabled,
                state.backgroundColor,

            ),
            state.backGroundCheckersColors,
        )
    }
}

@Suppress("LongMethod")
@Composable
fun PixelCanvasSnapshot(
    modifier: Modifier = Modifier,
    pixelImage: PixelImageModel,
    isFitAvailableSpace: Boolean = false,
    backgroundColor: Color? = Color.White,
) {
    val offsetSaver = createOffsetSaver()
    val intSizeSaver = createIntSizeSaver()
    val imageOffset = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset(0F, 0F)) }
    val margin = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset(0F, 0F)) }
    val lastCanvasSize =
        rememberSaveable(stateSaver = intSizeSaver) { mutableStateOf(IntSize(-1, -1)) }
    val zoomFactor = rememberSaveable { mutableFloatStateOf(NO_ZOOM_FACTOR) }

    val imagePixelSize = IntSize(pixelImage.getPixelWidth(), pixelImage.getPixelHeight())

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
                onCanvasSizeChanged(size, lastCanvasSize, imageOffset)
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_PREVIEW, zoomFactor.floatValue)

                    onCanvasPan(
                        pan = dragAmount,
                        CanvasContext(
                            pixelSizeInt,
                            imagePixelSize,
                            canvasSize = size,
                            imageOffset.value,
                        ),
                        updateImageOffset = { newOffset -> imageOffset.value = newOffset },
                    )
                }
            },
    ) {
        zoomFactor.floatValue = if (isFitAvailableSpace) {
            val pixelSizeNoZoom = PIXEL_SIZE_DP_PREVIEW.dp.toPx()
            val fitWidthZoomFactor = (size.width / (imagePixelSize.width * pixelSizeNoZoom)).toInt()
            val fitHeightZoomFactor =
                (size.height / (imagePixelSize.height * pixelSizeNoZoom)).toInt()
            max(NO_ZOOM_FACTOR, min(fitWidthZoomFactor, fitHeightZoomFactor).toFloat())
        } else {
            NO_ZOOM_FACTOR
        }
        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_PREVIEW, zoomFactor.floatValue)
        val canvasSize = size
        val imageSize = Size(
            width = (imagePixelSize.width * pixelSizeInt).toFloat(),
            height = (imagePixelSize.height * pixelSizeInt).toFloat(),
        )

        adjustImageOffset(imageOffset, margin, canvasSize, imageSize)

        drawCanvas(
            state = CanvasDrawState(
                drawImageState = CanvasDrawImageState(
                    pixelImage,
                    pixelSelection = null,
                    pixelSizeInt,
                    imageOffset.value,
                    imagePixelSize,
                    imageSize,
                    margin.value,
                ),
                floatingPixelImage = null,
                isShowBorderEnabled = true,
                isShowGridEnabled = false,
                backgroundColor,
            ),
            backGroundCheckersColors = null,
        )
    }
}

private fun onCanvasSizeChanged(
    size: IntSize,
    lastCanvasSize: MutableState<IntSize>,
    imageOffset: MutableState<Offset>,
) {
    val lastWidth = lastCanvasSize.value.width
    val lastHeight = lastCanvasSize.value.height
    val currentWidth = size.width
    val currentHeight = size.height

    if ((lastWidth >= 0) && (currentWidth > lastWidth)) {
        val widthIncrease = currentWidth - lastWidth
        val imageOffsetX = imageOffset.value.x
        imageOffset.value = Offset(
            max(0F, imageOffsetX - widthIncrease),
            imageOffset.value.y,
        )
    }

    if ((lastHeight >= 0) && (currentHeight > lastHeight)) {
        val heightIncrease = currentHeight - lastHeight
        val imageOffsetY = imageOffset.value.y
        imageOffset.value = Offset(
            imageOffset.value.x,
            max(0F, imageOffsetY - heightIncrease),
        )
    }

    lastCanvasSize.value = IntSize(currentWidth, currentHeight)
}

private fun onCanvasPan(
    pan: Offset,
    canvasContext: CanvasContext,
    updateImageOffset: (Offset) -> Unit,
) = with(canvasContext) {
    val imageWidthInt = imagePixelSize.width * pixelSizeInt
    val imageHeightInt = imagePixelSize.height * pixelSizeInt

    val minImageOffset = Offset(
        x = 0F,
        y = 0F,
    )
    val maxImageOffset = Offset(
        x = max(imageWidthInt - canvasSize.width, 0).toFloat(),
        y = max(imageHeightInt - canvasSize.height, 0).toFloat(),
    )

    val imageOffsetX = imageOffset.x
    val imageOffsetY = imageOffset.y

    updateImageOffset(
        Offset(
            x = (imageOffsetX - pan.x).cap(minImageOffset.x, maxImageOffset.x),
            y = (imageOffsetY - pan.y).cap(minImageOffset.y, maxImageOffset.y),
        ),
    )
}

private fun onCanvasZoom(
    zoomContext: ZoomContext,
    canvasContext: CanvasContext,
    onZoomUpdate: (Float) -> Unit,
    updateImageOffset: (Offset) -> Unit,
): Zoom = with(zoomContext) {
    var zoomAcc = zoom.acc + zoomDelta - 1F
    var zoomFactor = zoom.factor

    if (abs(zoomAcc) <= ZOOM_STEP) {
        return Zoom(zoomAcc, zoomFactor)
    }

    if (zoomAcc > 0) {
        zoomAcc -= ZOOM_STEP
        zoomFactor = min(MAX_ZOOM_FACTOR, zoomFactor + ZOOM_STEP)
    } else {
        zoomAcc += ZOOM_STEP
        zoomFactor = max(MIN_ZOOM_FACTOR, zoomFactor - ZOOM_STEP)
    }

    if (zoomFactor > MIN_ZOOM_FACTOR && zoomFactor < MAX_ZOOM_FACTOR) {
        val centroidFactor = if (zoomAcc > 0) -ZOOM_STEP else ZOOM_STEP

        val zoomPan = Offset(
            x = zoomCentroid.x * centroidFactor,
            y = zoomCentroid.y * centroidFactor,
        )

        onCanvasPan(
            pan = zoomPan,
            canvasContext,
            updateImageOffset,
        )
    }

    onZoomUpdate(zoomFactor)

    return Zoom(zoomAcc, zoomFactor)
}

private fun getPixelCoordinatesAt(
    offset: Offset,
    canvasContext: CanvasContext,
    margin: Offset,
): Pair<Int, Int>? = with(canvasContext) {
    if (offset.x - margin.x !in 0F..canvasSize.width.toFloat() ||
        offset.y - margin.y !in 0F..canvasSize.height.toFloat()
    ) {
        return null
    }

    val xPixel = ((imageOffset.x + offset.x - margin.x) / pixelSizeInt).toInt()
    val yPixel =
        imagePixelSize.height - 1 - ((imageOffset.y + offset.y - margin.y) / pixelSizeInt).toInt()

    return if (xPixel !in 0..<imagePixelSize.width || yPixel !in 0..<imagePixelSize.height) {
        null
    } else {
        Pair(xPixel, yPixel)
    }
}

private fun adjustImageOffset(
    imageOffset: MutableState<Offset>,
    margin: MutableState<Offset>,
    canvasSize: Size,
    imageSize: Size,
) {
    var imageOffsetX = imageOffset.value.x
    var imageOffsetY = imageOffset.value.y
    val marginX: Float
    val marginY: Float

    if (canvasSize.width > imageSize.width) {
        imageOffsetX = 0F
        marginX = (canvasSize.width - imageSize.width) / 2
    } else {
        imageOffsetX = adjustImageDimensionOffset(
            currentImageDimensionOffset = imageOffsetX,
            canvasDimensionValue = canvasSize.width,
            imageDimensionValue = imageSize.width,
        )
        marginX = 0F
    }

    if (canvasSize.height > imageSize.height) {
        imageOffsetY = 0F
        marginY = (canvasSize.height - imageSize.height) / 2
    } else {
        imageOffsetY = adjustImageDimensionOffset(
            currentImageDimensionOffset = imageOffsetY,
            canvasDimensionValue = canvasSize.height,
            imageDimensionValue = imageSize.height,
        )
        marginY = 0F
    }

    imageOffset.value = Offset(imageOffsetX, imageOffsetY)
    margin.value = Offset(marginX, marginY)
}

private fun adjustImageDimensionOffset(
    currentImageDimensionOffset: Float,
    canvasDimensionValue: Float,
    imageDimensionValue: Float,
) = if (currentImageDimensionOffset > 0) {
    if (imageDimensionValue <= canvasDimensionValue) {
        0F
    } else {
        val offsetSurplus = canvasDimensionValue + currentImageDimensionOffset - imageDimensionValue
        if (offsetSurplus > 0) {
            currentImageDimensionOffset - offsetSurplus
        } else {
            currentImageDimensionOffset
        }
    }
} else {
    currentImageDimensionOffset
}

private fun createOffsetSaver() = Saver<Offset, SizeF>(
    save = { offset -> SizeF(offset.x, offset.y) },
    restore = { sizeF -> Offset(sizeF.width, sizeF.height) },
)

private fun createIntSizeSaver() = Saver<IntSize, android.util.Size>(
    save = { intSize -> android.util.Size(intSize.width, intSize.height) },
    restore = { size -> IntSize(size.width, size.height) },
)

private fun Float.cap(min: Float, max: Float): Float = when {
    this < min -> min
    this > max -> max
    else -> this
}

fun createCheckersPixelImage(
    width: Int,
    height: Int,
    color1: Long,
    color2: Long,
): PixelImageModel {
    val rows = mutableListOf<List<PixelModel>>()
    for (y in 0..<height) {
        val row = mutableListOf<PixelModel>()
        for (x in 0..<width) {
            val isXEven = x % 2 == 0
            val isYEven = y % 2 == 0
            row.add(
                PixelModel(if (isXEven xor isYEven) 0 else 1),
            )
        }
        rows.add(row)
    }

    return PixelImageModel(
        pixelMatrixModel = PixelMatrixModel(
            content = rows,
        ),
        paletteModel = PaletteModel(listOf(color1, color2)),
    )
}

data class PixelCanvasState(
    val pixelImage: PixelImageModel,
    val pixelSelection: PixelSelectionModel? = null,
    val floatingPixelImage: FloatingPixelImageModel? = null,
    val initialZoomFactor: Float = NO_ZOOM_FACTOR,
    val isShowGridEnabled: Boolean = true,
    val backgroundColor: Color? = null,
    val backGroundCheckersColors: Pair<Color, Color>? = Pair(Color.Gray, Color.LightGray),
    val onPixelVisited: (xPixel: Int, yPixel: Int) -> Unit,
    val onPixelVisitCancel: () -> Unit,
    val onPixelVisitFinish: () -> Unit,
    val onZoomUpdate: (Float) -> Unit,
)

data class CanvasDrawState(
    val drawImageState: CanvasDrawImageState,
    val floatingPixelImage: FloatingPixelImageModel?,
    val isShowBorderEnabled: Boolean,
    val isShowGridEnabled: Boolean,
    val backgroundColor: Color?,
)

data class CanvasDrawImageState(
    val pixelImage: PixelImageModel,
    val pixelSelection: PixelSelectionModel?,
    val pixelSizeInt: Int,
    val imageOffset: Offset,
    val imagePixelSize: IntSize,
    val imageSize: Size,
    val margin: Offset,
)

data class PixelDrawState(
    val pixel: PixelModel,
    val palette: List<Color>,
    val visibleSize: Size,
    val xMatrixCoordinate: Int,
    val yMatrixCoordinate: Int,
    val offset: Offset,
)

private data class CanvasContext(
    val pixelSizeInt: Int,
    val imagePixelSize: IntSize,
    val canvasSize: IntSize,
    val imageOffset: Offset,
)

private data class ZoomContext(val zoom: Zoom, val zoomDelta: Float, val zoomCentroid: Offset)

private data class Zoom(val acc: Float, val factor: Float)
