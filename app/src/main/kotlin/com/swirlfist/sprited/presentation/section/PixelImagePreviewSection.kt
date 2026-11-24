package com.swirlfist.sprited.presentation.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.sprited.R
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.toHexCode
import com.swirlfist.sprited.presentation.state.CanvasSectionState
import com.swirlfist.sprited.presentation.state.PixelImagePreviewSectionState
import com.swirlfist.sprited.presentation.theme.SpritedTheme
import com.swirlfist.sprited.presentation.uielements.PaletteColorSelectButton
import com.swirlfist.sprited.presentation.uielements.PaletteColorSelectButtonState
import com.swirlfist.sprited.presentation.uielements.PixelCanvasSnapshot
import com.swirlfist.sprited.presentation.uielements.createCheckersPixelImage

@Composable
fun PixelImagePreviewSection(
    modifier: Modifier = Modifier,
    previewState: PixelImagePreviewSectionState,
    canvasState: CanvasSectionState,
) {
    val pixelImage = canvasState.pixelImageModel
    val selectedBackgroundColor = Color.fromColorLong(
        previewState.selectedPreviewBackgroundColorLong,
    )

    if (pixelImage != null) {
        Row(
            modifier = modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PixelCanvasSnapshot(
                modifier = modifier
                    .weight(1F),
                pixelImage = pixelImage,
                isFitAvailableSpace = previewState.isFitAvailableSpace,
                backgroundColor = selectedBackgroundColor,
            )
            PixelImagePreviewBackgroundColorSelection(
                selectedBackgroundColor = selectedBackgroundColor,
                onPreviewBackgroundColorSelected = previewState.onPreviewBackgroundColorSelected,
            )
        }
    }
}

@Composable
fun PixelImagePreviewBackgroundColorSelection(
    modifier: Modifier = Modifier,
    selectedBackgroundColor: Color,
    onPreviewBackgroundColorSelected: (Color) -> Unit,
) {
    FlowRow(
        maxLines = 2,
        modifier = modifier,
    ) {
        PreviewBackgroundColorButton(
            color = Color.Black,
            selectedBackgroundColor = selectedBackgroundColor,
            onPreviewBackgroundColorSelected,
        )
        PreviewBackgroundColorButton(
            color = Color.DarkGray,
            selectedBackgroundColor = selectedBackgroundColor,
            onPreviewBackgroundColorSelected,
        )
        PreviewBackgroundColorButton(
            color = Color.LightGray,
            selectedBackgroundColor = selectedBackgroundColor,
            onPreviewBackgroundColorSelected,
        )
        PreviewBackgroundColorButton(
            color = Color.White,
            selectedBackgroundColor = selectedBackgroundColor,
            onPreviewBackgroundColorSelected,
        )
    }
}

@Composable
fun PreviewBackgroundColorButton(
    color: Color,
    selectedBackgroundColor: Color,
    onPreviewBackgroundColorSelected: (Color) -> Unit,
) {
    PaletteColorSelectButton(
        state = PaletteColorSelectButtonState(
            color = color,
            size = 36.dp,
            isEnabled = true,
            contentDescriptionValue = stringResource(
                R.string.cd_preview_section_button_pick_background_color,
                color.toHexCode(),
            ),
            isSelected = selectedBackgroundColor == color,
            onClick = { onPreviewBackgroundColorSelected(color) },
        ),
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PixelImagePreviewSectionFitPreview() {
    SpritedTheme {
        PixelImagePreviewSection(
            modifier = Modifier.fillMaxSize(),
            previewState = PixelImagePreviewSectionState(
                onPreviewBackgroundColorSelected = {},
            ).copy(
                isFitAvailableSpace = true,
            ),
            canvasState = CanvasSectionState(
                pixelImageModel = createCheckersPixelImage(
                    width = 32,
                    height = 32,
                    color1 = Color.Black.toColorLong(),
                    color2 = Color.Yellow.toColorLong(),
                ),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PixelImagePreviewSectionNoFitPreview() {
    SpritedTheme {
        PixelImagePreviewSection(
            modifier = Modifier.fillMaxSize(),
            previewState = PixelImagePreviewSectionState(
                onPreviewBackgroundColorSelected = {},
            ).copy(
                isFitAvailableSpace = false,
            ),
            canvasState = CanvasSectionState(
                pixelImageModel = createCheckersPixelImage(
                    width = 32,
                    height = 32,
                    color1 = Color.Black.toColorLong(),
                    color2 = Color.Yellow.toColorLong(),
                ),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 96, heightDp = 96)
@Composable
fun PixelImagePreviewSectionEmptyNoFitPreview() {
    SpritedTheme {
        PixelImagePreviewSection(
            modifier = Modifier.fillMaxSize(),
            previewState = PixelImagePreviewSectionState(
                onPreviewBackgroundColorSelected = {},
            ).copy(
                isFitAvailableSpace = false,
            ),
            canvasState = CanvasSectionState(
                pixelImageModel = PixelImageModel.createEmpty(
                    width = 32,
                    height = 32,
                    colors = listOf(Color.Black.toColorLong(), Color.Yellow.toColorLong()),
                ),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 240, heightDp = 240)
@Preview
@Composable
fun PixelImagePreviewBackgroundColorSelectionPreview() {
    SpritedTheme {
        PixelImagePreviewBackgroundColorSelection(
            modifier = Modifier,
            selectedBackgroundColor = Color.White,
            onPreviewBackgroundColorSelected = {},
        )
    }
}
