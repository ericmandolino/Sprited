package com.swirlfist.sprited.presentation.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swirlfist.sprited.presentation.theme.SpritedTheme

@Composable
fun PaletteColorButton(modifier: Modifier = Modifier, state: PaletteColorButtonState) {
    Button(
        modifier = modifier
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = IconButtonDefaults.filledIconButtonColors().containerColor,
                ),
                shape = RoundedCornerShape(4.dp),
            )
            .size(state.size)
            .semantics {
                contentDescription = state.contentDescriptionValue
            },
        onClick = state.onClick,
        shape = RoundedCornerShape(4.dp),
        enabled = state.isEnabled,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = state.color,
            contentColor = state.color,
        ),
    ) {
    }
}

@Composable
fun PaletteColorSelectButton(state: PaletteColorSelectButtonState) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .selectedButtonModifier(
                state.isSelected,
                IconButtonDefaults.filledIconButtonColors().containerColor,
            ),
    ) {
        PaletteColorButton(
            modifier = Modifier,
            state = state.toPaletteColorButtonState(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaletteColorSelectButtonPreview() {
    SpritedTheme {
        PaletteColorSelectButton(
            state = PaletteColorSelectButtonState(
                color = Color.Red,
                size = 48.dp,
                isEnabled = true,
                contentDescriptionValue = "",
                isSelected = true,
                onClick = {},
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaletteColorButtonPreview() {
    SpritedTheme {
        PaletteColorButton(
            state = PaletteColorButtonState(
                color = Color.Red,
                size = 48.dp,
                isEnabled = true,
                contentDescriptionValue = "",
                onClick = {},
            ),
        )
    }
}

data class PaletteColorButtonState(
    val color: Color,
    val size: Dp,
    val isEnabled: Boolean,
    val contentDescriptionValue: String,
    val onClick: () -> Unit,
)

data class PaletteColorSelectButtonState(
    val color: Color,
    val size: Dp,
    val isEnabled: Boolean,
    val contentDescriptionValue: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)

private fun PaletteColorSelectButtonState.toPaletteColorButtonState() = PaletteColorButtonState(
    color,
    size,
    isEnabled,
    contentDescriptionValue,
    onClick,
)
