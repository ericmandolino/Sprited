@file:Suppress("TooManyFunctions")

package com.swirlfist.sprited.presentation.uielements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.swirlfist.sprited.domain.getPixelAt
import com.swirlfist.sprited.domain.getPixelHeight
import com.swirlfist.sprited.domain.getPixelWidth
import com.swirlfist.sprited.domain.isSelected
import com.swirlfist.sprited.domain.model.FloatingPixelImageModel
import com.swirlfist.sprited.domain.model.PixelModel
import com.swirlfist.sprited.domain.model.PixelSelectionModel
import com.swirlfist.sprited.domain.model.RectangleSelectionModel
import com.swirlfist.sprited.domain.selectNonEmpty
import com.swirlfist.sprited.presentation.getColor
import kotlin.math.max
import kotlin.math.min

private const val GRID_LINE_WIDTH_DP = 1
private const val FLOATING_MOVE_HINT_TRIANGLE_ALPHA = 0.5F
private const val FLOATING_MOVE_HINT_TRIANGLE_MARGIN_DP = 8
private const val FLOATING_MOVE_HINT_TRIANGLE_WIDTH_DP = 48
private const val FLOATING_MOVE_HINT_TRIANGLE_HEIGHT_DP = 36
private const val FLOATING_MOVE_HINT_TRIANGLE_STROKE_WIDTH_DP = 4F
private const val FLOATING_STROKE_WIDTH_DP = 3
private const val FLOATING_STROKE_DASH_INTERVAL = 10F
private const val SELECTION_STROKE_WIDTH_DP = 3
private const val SELECTION_STROKE_DASH_INTERVAL = 10F
private const val SELECTION_RESIZE_SQUARE_SIZE_DP = 16
private const val SELECTION_RESIZE_SQUARE_BORDER_WIDTH_DP = 1
private const val TRIANGLE_VERTEX_1_X_INDEX = 0
private const val TRIANGLE_VERTEX_1_Y_INDEX = 1
private const val TRIANGLE_VERTEX_2_X_INDEX = 2
private const val TRIANGLE_VERTEX_2_Y_INDEX = 3
private const val TRIANGLE_VERTEX_3_X_INDEX = 4
private const val TRIANGLE_VERTEX_3_Y_INDEX = 5

fun Density.getPixelSizeInt(dpSize: Int, zoomFactor: Float) =
    (dpSize.dp.toPx() * zoomFactor).toInt()

fun DrawScope.drawCanvas(state: CanvasDrawState, backGroundCheckersColors: Pair<Color, Color>?) =
    with(state) {
        val pixelImage = drawImageState.pixelImage
        val pixelSelection = drawImageState.pixelSelection
        val canvasSize = size
        val imageSize = drawImageState.imageSize
        val palette = pixelImage.paletteModel.colors.map { color -> Color.fromColorLong(color) }

        state.backgroundColor?.let { color ->
            drawRect(
                color,
                topLeft = drawImageState.margin,
                size = imageSize,
            )
        }

        val imageLines = drawImageInCanvas(
            state = state.drawImageState,
            palette,
            backGroundCheckersColors,
        )

        val floatingImageLines = drawFloatingImage(
            floatingPixelImage,
            floatingImageContext = FloatingImageContext(
                drawImageState.pixelSizeInt,
                drawImageState.imagePixelSize,
                drawImageState.imageSize,
                drawImageState.imageOffset,
            ),
            drawImageState.margin,
        )

        if (state.isShowGridEnabled) {
            drawGridLines(imageLines, state)
        }

        if (state.isShowBorderEnabled) {
            drawBorders(imageSize, canvasSize, drawImageState.margin)
        }

        drawSelectionLines(
            imageLines.selectionLines,
            isFloatingImage = false,
        )
        drawRectangleSelection(
            rectangleSelection = pixelSelection?.rectangleSelection,
            drawImageState.pixelSizeInt,
            canvasSize,
            drawImageState.imageOffset,
            drawImageState.margin,
        )
        drawSelectionLines(
            floatingImageLines.selectionLines,
            isFloatingImage = true,
        )

        if (floatingPixelImage != null) {
            drawFloatingImageMoveHints()
        }
    }

private fun DrawScope.drawGridLines(imageLines: ImageLines, state: CanvasDrawState) = with(state) {
    val canvasSize = size
    val imageSize = drawImageState.imageSize
    val gridLineWidth = GRID_LINE_WIDTH_DP.dp.toPx()

    imageLines.horizontalGridLineYCoordinates.forEach { y ->
        drawHorizontalGridLine(
            y,
            canvasSize,
            imageSize,
            gridLineWidth,
            drawImageState.margin.x,
        )
    }

    imageLines.verticalGridLineXCoordinates.forEach { x ->
        drawVerticalGridLine(
            x,
            canvasSize,
            imageSize,
            gridLineWidth,
            drawImageState.margin.y,
        )
    }
}

private fun DrawScope.drawImageInCanvas(
    state: CanvasDrawImageState,
    palette: List<Color>,
    backGroundCheckersColors: Pair<Color, Color>?,
    floatingOffset: Pair<Int, Int> = Pair(0, 0),
): ImageLines = with(state) {
    val canvasSize = size
    val horizontalGridLineYCoordinates = mutableListOf<Float>()
    val verticalGridLineXCoordinates = mutableListOf<Float>()
    val selectionLines = mutableListOf<Pair<Offset, Offset>>()

    val margin = state.margin
    val imageOffset = state.imageOffset

    var y = margin.y
    var yMatrixCoordinate =
        imagePixelSize.height - 1 - ((imageOffset.y + y - margin.y) / pixelSizeInt).toInt()

    while (y < canvasSize.height && yMatrixCoordinate >= 0) {
        val adjustedPixelHeight = pixelSizeInt.adjustPixelDimension(y, imageOffset.y, margin.y)
        val adjustedYMatrixCoordinate = yMatrixCoordinate - floatingOffset.second

        if (adjustedYMatrixCoordinate in 0..<pixelImage.getPixelHeight()) {
            drawImageRowInCanvas(
                context = ImageRowDrawContext(
                    y,
                    yMatrixCoordinate,
                    adjustedYMatrixCoordinate,
                    adjustedPixelHeight,
                    palette,
                    floatingOffset,
                    horizontalGridLineYCoordinates,
                    verticalGridLineXCoordinates,
                    selectionLines,
                ),
                state,
                backGroundCheckersColors,
            )
        }

        y += adjustedPixelHeight
        yMatrixCoordinate--
    }

    if (yMatrixCoordinate == -1 && y <= canvasSize.height) {
        horizontalGridLineYCoordinates.add(y)
    }

    return ImageLines(
        verticalGridLineXCoordinates.toList(),
        horizontalGridLineYCoordinates.toList(),
        selectionLines.toList(),
    )
}

private fun Int.adjustPixelDimension(coordinate: Float, imageOffset: Float, margin: Float) =
    if (coordinate - margin > 0F || imageOffset == 0F) {
        this
    } else {
        this - imageOffset.toInt() % this
    }

private fun DrawScope.drawImageRowInCanvas(
    context: ImageRowDrawContext,
    state: CanvasDrawImageState,
    backGroundCheckersColors: Pair<Color, Color>?,
) = with(state) {
    val canvasSize = size
    val yMatrixCoordinate = context.yMatrixCoordinate
    val adjustedYMatrixCoordinate = context.adjustedYMatrixCoordinate
    val y = context.y

    var x = margin.x
    var xMatrixCoordinate = ((imageOffset.x + x - margin.x) / pixelSizeInt).toInt()

    while (x < canvasSize.width && xMatrixCoordinate < imagePixelSize.width) {
        val adjustedPixelWidth = pixelSizeInt.adjustPixelDimension(
            x,
            imageOffset.x,
            margin.x,
        )

        val adjustedXMatrixCoordinate = xMatrixCoordinate - context.floatingOffset.first
        if (adjustedXMatrixCoordinate in 0..<pixelImage.getPixelWidth()) {
            val adjustedPixelHeight = context.adjustedPixelHeight
            val matrixCoordinates = IntOffset(xMatrixCoordinate, yMatrixCoordinate)
            val adjustedMatrixCoordinates = IntOffset(
                x = adjustedXMatrixCoordinate,
                y = adjustedYMatrixCoordinate,
            )
            drawPixelInCanvas(
                PixelDrawContext(
                    pixelOffset = Offset(x, y),
                    adjustedPixelSize = IntSize(adjustedPixelWidth, adjustedPixelHeight),
                    matrixCoordinates,
                    adjustedMatrixCoordinates,
                    context.palette,
                    context.selectionLines,
                ),
                state,
                backGroundCheckersColors,
            )
            context.verticalGridLineXCoordinates.add(x)
        }

        x += adjustedPixelWidth
        xMatrixCoordinate++
    }

    if (xMatrixCoordinate == imagePixelSize.width && x <= canvasSize.width) {
        context.verticalGridLineXCoordinates.add(x)
    }

    context.horizontalGridLineYCoordinates.add(y)
}

private fun DrawScope.drawPixelInCanvas(
    pixelDrawContext: PixelDrawContext,
    state: CanvasDrawImageState,
    backGroundCheckersColors: Pair<Color, Color>?,
) = with(state) {
    val canvasSize = size
    val pixelOffset = pixelDrawContext.pixelOffset
    val xMatrixCoordinate = pixelDrawContext.matrixCoordinates.x
    val yMatrixCoordinate = pixelDrawContext.matrixCoordinates.y
    val adjustedXMatrixCoordinate = pixelDrawContext.adjustedMatrixCoordinates.x
    val adjustedYMatrixCoordinate = pixelDrawContext.adjustedMatrixCoordinates.y

    val pixel = pixelImage.getPixelAt(
        x = adjustedXMatrixCoordinate,
        y = adjustedYMatrixCoordinate,
    )

    val visiblePixelWidth =
        min(pixelDrawContext.adjustedPixelSize.width.toFloat(), canvasSize.width - pixelOffset.x)
    val visiblePixelHeight =
        min(pixelDrawContext.adjustedPixelSize.height.toFloat(), canvasSize.height - pixelOffset.y)

    drawPixel(
        state = PixelDrawState(
            pixel,
            palette = pixelDrawContext.palette,
            visibleSize = Size(visiblePixelWidth, visiblePixelHeight),
            xMatrixCoordinate,
            yMatrixCoordinate,
            offset = pixelOffset,
        ),
        backGroundCheckersColors,
    )

    addSelectionLines(
        PixelSelectionContext(
            state.pixelSelection,
            pixelSizeInt,
            visiblePixelWidth,
            visiblePixelHeight,
            adjustedXMatrixCoordinate,
            adjustedYMatrixCoordinate,
            pixelOffset,
            canvasSize,
        ),
        pixelDrawContext.selectionLines,
    )
}

private fun DrawScope.drawPixel(
    state: PixelDrawState,
    backGroundCheckersColors: Pair<Color, Color>?,
) {
    val isXMatrixCoordinateEven = state.xMatrixCoordinate % 2 == 0
    val isYMatrixCoordinateEven = state.yMatrixCoordinate % 2 == 0
    val pixelColor = getColor(
        state.pixel,
        state.palette,
        isXMatrixCoordinateEven,
        isYMatrixCoordinateEven,
        backGroundCheckersColors,
    )

    pixelColor?.let { color ->
        drawRect(
            color,
            topLeft = state.offset,
            size = state.visibleSize,
        )
    }
}

private fun DrawScope.drawBorders(imageSize: Size, canvasSize: Size, margin: Offset) {
    val gridLineWidth = GRID_LINE_WIDTH_DP.dp.toPx()

    if (imageSize.width <= canvasSize.width) {
        drawVerticalGridLine(
            margin.x,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.y,
        )
        drawVerticalGridLine(
            canvasSize.width - margin.x,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.y,
        )
    }
    if (imageSize.height <= canvasSize.height) {
        drawHorizontalGridLine(
            margin.y,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.x,
        )
        drawHorizontalGridLine(
            canvasSize.height - margin.y,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.x,
        )
    }
}

private fun DrawScope.drawSelectionLines(
    selectionLines: List<Pair<Offset, Offset>>,
    isFloatingImage: Boolean,
) {
    if (selectionLines.isEmpty()) {
        return
    }

    val strokeWidth = if (isFloatingImage) {
        FLOATING_STROKE_WIDTH_DP.dp.toPx()
    } else {
        SELECTION_STROKE_WIDTH_DP.dp.toPx()
    }

    val interval = if (isFloatingImage) {
        FLOATING_STROKE_DASH_INTERVAL
    } else {
        SELECTION_STROKE_DASH_INTERVAL
    }

    selectionLines.forEach { (start, end) ->
        drawSelectionLine(start, end, strokeWidth, interval)
    }
}

private fun DrawScope.drawSelectionLine(
    start: Offset,
    end: Offset,
    strokeWidth: Float,
    interval: Float,
    colors: Pair<Color, Color> = Pair(Color.Black, Color.White),
) {
    drawLine(
        color = colors.first,
        strokeWidth = strokeWidth,
        start = start,
        end = end,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(interval, interval),
            phase = 0F,
        ),
    )
    drawLine(
        color = colors.second,
        strokeWidth = strokeWidth,
        start = start,
        end = end,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(interval, interval),
            phase = 3 * interval,
        ),
    )
}

private fun DrawScope.drawRectangleSelection(
    rectangleSelection: RectangleSelectionModel?,
    pixelSizeInt: Int,
    canvasSize: Size,
    imageOffset: Offset,
    margin: Offset,
) {
    if (rectangleSelection == null) {
        return
    }

    val strokeWidth = SELECTION_STROKE_WIDTH_DP.dp.toPx()
    val resizeSquareSize = SELECTION_RESIZE_SQUARE_SIZE_DP.dp.toPx()
    val resizeSquareBorderWidth = SELECTION_RESIZE_SQUARE_BORDER_WIDTH_DP.dp.toPx()

    val rectStart = rectangleSelection.start
    val rectEnd = rectangleSelection.end
    val selectionRect = Rect(
        topLeft = Offset(
            x = min(rectStart.first, rectEnd.first).toFloat(),
            y = min(rectStart.second, rectEnd.second).toFloat(),
        ),
        bottomRight = Offset(
            x = max(rectStart.first, rectEnd.first).toFloat(),
            y = max(rectStart.second, rectEnd.second).toFloat(),
        ),
    )
    val xSelectionOffset = selectionRect.topLeft.x * pixelSizeInt
    val ySelectionOffset = selectionRect.topLeft.y * pixelSizeInt
    val rectMinX = xSelectionOffset - imageOffset.x + margin.x
    val rectMinY = ySelectionOffset - imageOffset.y + margin.y
    val rectMaxX = rectMinX + selectionRect.width * pixelSizeInt
    val rectMaxY = rectMinY + selectionRect.height * pixelSizeInt

    val minXVisible = rectMinX >= 0 && rectMinX < canvasSize.width
    val maxXVisible = rectMaxX >= 0 && rectMaxX < canvasSize.width
    val minYVisible = rectMinY >= 0 && rectMinY < canvasSize.height
    val maxYVisible = rectMaxY >= 0 && rectMaxY < canvasSize.height

    val rectangleSelectionContext = RectangleSelectionContext(
        rectMinX,
        rectMaxX,
        rectMinY,
        rectMaxY,
        minXVisible,
        maxXVisible,
        minYVisible,
        maxYVisible,
        canvasSize,
    )

    drawRectangleSelectionLines(
        rectangleSelectionContext,
        strokeWidth,
    )

    drawRectangleSelectionCornerSquares(
        rectangleSelectionContext,
        resizeSquareSize,
        resizeSquareBorderWidth,
    )

    drawRectangleSelectionMiddleSquares(
        rectangleSelectionContext,
        resizeSquareSize,
        resizeSquareBorderWidth,
    )
}

private fun DrawScope.drawRectangleSelectionLines(
    context: RectangleSelectionContext,
    strokeWidth: Float,
) {
    if (context.minYVisible) {
        drawRectangleSelectionHorizontalLine(
            startX = max(0F, context.rectMinX),
            endX = min(context.canvasSize.width, context.rectMaxX),
            y = context.rectMinY,
            strokeWidth,
        )
    }

    if (context.maxYVisible) {
        drawRectangleSelectionHorizontalLine(
            startX = max(0F, context.rectMinX),
            endX = min(context.canvasSize.width, context.rectMaxX),
            y = context.rectMaxY,
            strokeWidth,
        )
    }

    if (context.minXVisible) {
        drawRectangleSelectionVerticalLine(
            startY = max(0F, context.rectMinY),
            endY = min(context.canvasSize.height, context.rectMaxY),
            x = context.rectMinX,
            strokeWidth,
        )
    }

    if (context.maxXVisible) {
        drawRectangleSelectionVerticalLine(
            startY = max(0F, context.rectMinY),
            endY = min(context.canvasSize.height, context.rectMaxY),
            x = context.rectMaxX,
            strokeWidth,
        )
    }
}

private fun DrawScope.drawRectangleSelectionCornerSquares(
    context: RectangleSelectionContext,
    resizeSquareSize: Float,
    resizeSquareBorderWidth: Float,
) {
    val lowerLeftCornerVisible = context.minXVisible && context.maxYVisible
    val upperLeftCornerVisible = context.minXVisible && context.minYVisible
    val lowerRightCornerVisible = context.maxXVisible && context.maxYVisible
    val upperRightCornerVisible = context.maxXVisible && context.minYVisible

    if (lowerLeftCornerVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(context.rectMinX, context.rectMaxY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (upperLeftCornerVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(context.rectMinX, context.rectMinY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (lowerRightCornerVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(context.rectMaxX, context.rectMaxY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (upperRightCornerVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(context.rectMaxX, context.rectMinY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }
}

private fun DrawScope.drawRectangleSelectionMiddleSquares(
    context: RectangleSelectionContext,
    resizeSquareSize: Float,
    resizeSquareBorderWidth: Float,
) {
    val rectMiddleX = context.rectMinX + (context.rectMaxX - context.rectMinX) / 2
    val rectMiddleY = context.rectMinY + (context.rectMaxY - context.rectMinY) / 2
    val middleXVisible = rectMiddleX >= 0 && rectMiddleX < context.canvasSize.width
    val middleYVisible = rectMiddleY >= 0 && rectMiddleY < context.canvasSize.height
    val lowerMiddleVisible = middleXVisible && context.maxYVisible
    val upperMiddleVisible = middleXVisible && context.minYVisible
    val leftMiddleVisible = context.minXVisible && middleYVisible
    val rightMiddleVisible = context.maxXVisible && middleYVisible

    if (lowerMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(rectMiddleX, context.rectMaxY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (upperMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(rectMiddleX, context.rectMinY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (leftMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(context.rectMinX, rectMiddleY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (rightMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            offset = Offset(context.rectMaxX, rectMiddleY),
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }
}

private fun DrawScope.drawRectangleSelectionHorizontalLine(
    startX: Float,
    endX: Float,
    y: Float,
    strokeWidth: Float,
) {
    val start = Offset(
        x = startX,
        y,
    )
    val end = Offset(
        x = endX,
        y,
    )
    drawSelectionLine(start, end, strokeWidth, SELECTION_STROKE_DASH_INTERVAL)
}

private fun DrawScope.drawRectangleSelectionVerticalLine(
    startY: Float,
    endY: Float,
    x: Float,
    strokeWidth: Float,
) {
    val start = Offset(
        x,
        y = startY,
    )
    val end = Offset(
        x,
        y = endY,
    )
    drawSelectionLine(start, end, strokeWidth, SELECTION_STROKE_DASH_INTERVAL)
}

private fun DrawScope.drawRectangleSelectionResizeSquare(
    offset: Offset,
    size: Float,
    borderWidth: Float,
    color: Color = Color.White,
    borderColor: Color = Color.Black,
) {
    val halfSize = size / 2
    drawRect(
        borderColor,
        topLeft = Offset(offset.x - halfSize, offset.y - halfSize),
        size = Size(size, size),
    )

    val borderlessSize = size - 2 * borderWidth
    val halfBorderlessSize = borderlessSize / 2

    drawRect(
        color,
        topLeft = Offset(offset.x - halfBorderlessSize, offset.y - halfBorderlessSize),
        size = Size(borderlessSize, borderlessSize),
    )
}

private fun DrawScope.drawFloatingImage(
    floatingPixelImage: FloatingPixelImageModel?,
    floatingImageContext: FloatingImageContext,
    margin: Offset,
): ImageLines = with(floatingImageContext) {
    if (floatingPixelImage == null) {
        return ImageLines()
    }

    val imageLines = drawImageInCanvas(
        state = CanvasDrawImageState(
            pixelImage = floatingPixelImage.pixelImageModel,
            pixelSelection = floatingPixelImage.pixelImageModel.selectNonEmpty(),
            pixelSizeInt,
            imageOffset,
            imagePixelSize,
            imageSize,
            margin,
        ),
        palette = floatingPixelImage.pixelImageModel.paletteModel.colors.map { color ->
            Color.fromColorLong(color)
        },
        backGroundCheckersColors = null,
        floatingOffset = floatingPixelImage.offset,
    )

    return imageLines
}

private fun DrawScope.drawHorizontalGridLine(
    y: Float,
    canvasSize: Size,
    imageSize: Size,
    width: Float,
    margin: Float,
) {
    drawGridLine(
        GridLine(
            isVertical = false,
            dimension = y,
            width,
        ),
        canvasSize,
        imageSize,
        margin,
    )
}

private fun DrawScope.drawVerticalGridLine(
    x: Float,
    canvasSize: Size,
    imageSize: Size,
    width: Float,
    margin: Float,
) {
    drawGridLine(
        GridLine(
            isVertical = true,
            dimension = x,
            width,
        ),
        canvasSize,
        imageSize,
        margin,
    )
}

private fun DrawScope.drawGridLine(
    gridLine: GridLine,
    canvasSize: Size,
    imageSize: Size,
    margin: Float,
) = with(gridLine) {
    val start: Offset
    val end: Offset

    if (isVertical) {
        start = Offset(
            dimension,
            margin,
        )
        end = Offset(
            dimension,
            min(canvasSize.height - margin, imageSize.height + margin),
        )
    } else {
        start = Offset(
            margin,
            dimension,
        )
        end = Offset(
            min(canvasSize.width - margin, imageSize.width + margin),
            dimension,
        )
    }

    drawLine(Color.DarkGray, start, end, width)
}

private fun DrawScope.drawFloatingImageMoveHints() {
    val triangleWidth = FLOATING_MOVE_HINT_TRIANGLE_WIDTH_DP.dp.toPx()
    val triangleHeight = FLOATING_MOVE_HINT_TRIANGLE_HEIGHT_DP.dp.toPx()
    val margin = FLOATING_MOVE_HINT_TRIANGLE_MARGIN_DP.dp.toPx()

    val centerWidth = size.width / 2
    val centerHeight = size.height / 2
    val halfTriangleWidth = triangleWidth / 2
    val halfTriangleHeight = triangleHeight / 2

    val topTriangleVertices = floatArrayOf(
        centerWidth,
        margin + halfTriangleHeight,
        centerWidth + halfTriangleWidth,
        margin + triangleHeight,
        centerWidth - halfTriangleWidth,
        margin + triangleHeight,
    )

    val bottomTriangleVertices = floatArrayOf(
        centerWidth,
        size.height - margin - halfTriangleHeight,
        centerWidth + halfTriangleWidth,
        size.height - margin - triangleHeight,
        centerWidth - halfTriangleWidth,
        size.height - margin - triangleHeight,
    )

    val leftTriangleVertices = floatArrayOf(
        margin + halfTriangleHeight,
        centerHeight,
        margin + triangleHeight,
        centerHeight - halfTriangleHeight,
        margin + triangleHeight,
        centerHeight + halfTriangleHeight,
    )

    val rightTriangleVertices = floatArrayOf(
        size.width - margin - halfTriangleHeight,
        centerHeight,
        size.width - margin - triangleHeight,
        centerHeight - halfTriangleHeight,
        size.width - margin - triangleHeight,
        centerHeight + halfTriangleHeight,
    )

    drawFloatingImageMoveHint(topTriangleVertices)
    drawFloatingImageMoveHint(bottomTriangleVertices)
    drawFloatingImageMoveHint(leftTriangleVertices)
    drawFloatingImageMoveHint(rightTriangleVertices)
}

private fun DrawScope.drawFloatingImageMoveHint(vertices: FloatArray) {
    val path = Path().apply {
        moveTo(vertices[TRIANGLE_VERTEX_1_X_INDEX], vertices[TRIANGLE_VERTEX_1_Y_INDEX])
        lineTo(vertices[TRIANGLE_VERTEX_2_X_INDEX], vertices[TRIANGLE_VERTEX_2_Y_INDEX])
        lineTo(vertices[TRIANGLE_VERTEX_3_X_INDEX], vertices[TRIANGLE_VERTEX_3_Y_INDEX])
        lineTo(vertices[TRIANGLE_VERTEX_1_X_INDEX], vertices[TRIANGLE_VERTEX_1_Y_INDEX])
    }

    drawPath(
        path,
        color = Color.Black.copy(alpha = FLOATING_MOVE_HINT_TRIANGLE_ALPHA),
        style = Fill,
    )

    drawPath(
        path,
        color = Color.White.copy(alpha = FLOATING_MOVE_HINT_TRIANGLE_ALPHA),
        style = Stroke(
            width = FLOATING_MOVE_HINT_TRIANGLE_STROKE_WIDTH_DP,
        ),
    )
}

private fun addSelectionLines(
    pixelSelectionContext: PixelSelectionContext,
    selectionLines: MutableList<Pair<Offset, Offset>>,
): Unit = with(pixelSelectionContext) {
    val isSelected = pixelSelection?.isSelected(
        x = xMatrixCoordinate,
        y = yMatrixCoordinate,
    ) ?: false

    if (!isSelected) {
        return
    }

    val fullWidth = visiblePixelWidth == pixelSizeInt.toFloat()
    val fullHeight = visiblePixelHeight == pixelSizeInt.toFloat()
    val sizeOffset = Offset(
        offset.x + visiblePixelWidth,
        offset.y + visiblePixelHeight,
    )

    val isSelectedAbove = pixelSelection.isSelected(
        x = xMatrixCoordinate,
        y = yMatrixCoordinate + 1,
    )
    val isSelectedBelow = pixelSelection.isSelected(
        x = xMatrixCoordinate,
        y = yMatrixCoordinate - 1,
    )
    val isSelectedLeft = pixelSelection.isSelected(
        x = xMatrixCoordinate - 1,
        y = yMatrixCoordinate,
    )
    val isSelectedRight = pixelSelection.isSelected(
        x = xMatrixCoordinate + 1,
        y = yMatrixCoordinate,
    )

    val isTopVisible = fullHeight || sizeOffset.y > pixelSizeInt
    val isBottomVisible = fullHeight || sizeOffset.y < canvasSize.height
    val isLeftVisible = fullWidth || sizeOffset.x > pixelSizeInt
    val isRightVisible = fullWidth || sizeOffset.x < canvasSize.width

    addSelectionLineTop(isSelectedAbove, isTopVisible, offset, sizeOffset, selectionLines)
    addSelectionLineBottom(isSelectedBelow, isBottomVisible, offset, sizeOffset, selectionLines)
    addSelectionLineLeft(isSelectedLeft, isLeftVisible, offset, sizeOffset, selectionLines)
    addSelectionLineRight(isSelectedRight, isRightVisible, offset, sizeOffset, selectionLines)
}

private fun addSelectionLineTop(
    isSelectedAbove: Boolean,
    isTopVisible: Boolean,
    offset: Offset,
    sizeOffset: Offset,
    selectionLines: MutableList<Pair<Offset, Offset>>,
) {
    if (!isSelectedAbove && isTopVisible) {
        selectionLines.add(
            Pair(offset, offset.copy(x = sizeOffset.x)),
        )
    }
}

private fun addSelectionLineBottom(
    isSelectedBelow: Boolean,
    isBottomVisible: Boolean,
    offset: Offset,
    sizeOffset: Offset,
    selectionLines: MutableList<Pair<Offset, Offset>>,
) {
    if (!isSelectedBelow && isBottomVisible) {
        selectionLines.add(
            Pair(offset.copy(y = sizeOffset.y), offset.copy(x = sizeOffset.x, y = sizeOffset.y)),
        )
    }
}

private fun addSelectionLineLeft(
    isSelectedLeft: Boolean,
    isLeftVisible: Boolean,
    offset: Offset,
    sizeOffset: Offset,
    selectionLines: MutableList<Pair<Offset, Offset>>,
) {
    if (!isSelectedLeft && isLeftVisible) {
        selectionLines.add(Pair(offset, offset.copy(y = sizeOffset.y)))
    }
}

private fun addSelectionLineRight(
    isSelectedRight: Boolean,
    isRightVisible: Boolean,
    offset: Offset,
    sizeOffset: Offset,
    selectionLines: MutableList<Pair<Offset, Offset>>,
) {
    if (!isSelectedRight && isRightVisible) {
        selectionLines.add(
            Pair(offset.copy(x = sizeOffset.x), offset.copy(x = sizeOffset.x, y = sizeOffset.y)),
        )
    }
}

private fun getColor(
    pixel: PixelModel,
    palette: List<Color>,
    isXMatrixCoordinateEven: Boolean,
    isYMatrixCoordinateEven: Boolean,
    backGroundCheckersColors: Pair<Color, Color>?,
): Color? = palette.getColor(pixel)
    ?: backGroundCheckersColors?.let { (first, second) ->
        if (isXMatrixCoordinateEven xor isYMatrixCoordinateEven) {
            first
        } else {
            second
        }
    }

private data class ImageLines(
    val verticalGridLineXCoordinates: List<Float> = listOf(),
    val horizontalGridLineYCoordinates: List<Float> = listOf(),
    val selectionLines: List<Pair<Offset, Offset>> = listOf(),
)

private data class GridLine(val isVertical: Boolean, val dimension: Float, val width: Float)

private data class RectangleSelectionContext(
    val rectMinX: Float,
    val rectMaxX: Float,
    val rectMinY: Float,
    val rectMaxY: Float,
    val minXVisible: Boolean,
    val maxXVisible: Boolean,
    val minYVisible: Boolean,
    val maxYVisible: Boolean,
    val canvasSize: Size,
)

private data class PixelSelectionContext(
    val pixelSelection: PixelSelectionModel?,
    val pixelSizeInt: Int,
    val visiblePixelWidth: Float,
    val visiblePixelHeight: Float,
    val xMatrixCoordinate: Int,
    val yMatrixCoordinate: Int,
    val offset: Offset,
    val canvasSize: Size,
)

private data class FloatingImageContext(
    val pixelSizeInt: Int,
    val imagePixelSize: IntSize,
    val imageSize: Size,
    val imageOffset: Offset,
)

private data class ImageRowDrawContext(
    val y: Float,
    val yMatrixCoordinate: Int,
    val adjustedYMatrixCoordinate: Int,
    val adjustedPixelHeight: Int,
    val palette: List<Color>,
    val floatingOffset: Pair<Int, Int> = Pair(0, 0),
    val horizontalGridLineYCoordinates: MutableList<Float>,
    val verticalGridLineXCoordinates: MutableList<Float>,
    val selectionLines: MutableList<Pair<Offset, Offset>>,
)

private data class PixelDrawContext(
    val pixelOffset: Offset,
    val adjustedPixelSize: IntSize,
    val matrixCoordinates: IntOffset,
    val adjustedMatrixCoordinates: IntOffset,
    val palette: List<Color>,
    val selectionLines: MutableList<Pair<Offset, Offset>>,
)
