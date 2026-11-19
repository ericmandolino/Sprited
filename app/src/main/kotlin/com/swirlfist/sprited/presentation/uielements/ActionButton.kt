package com.swirlfist.sprited.presentation.uielements

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swirlfist.sprited.R
import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.presentation.section.ActionButtonType
import com.swirlfist.sprited.presentation.section.ActionIconButtonType
import com.swirlfist.sprited.presentation.theme.SpritedTheme

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    actionButtonType: ActionButtonType,
    size: Dp = 48.dp,
    isEnabled: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .selectedButtonModifier(
                isSelected,
                IconButtonDefaults.filledIconButtonColors().containerColor,
            ),
    ) {
        when (actionButtonType) {
            ActionButtonType.NoParentActionUndoRedoButtonGroupType,
            ActionButtonType.NoParentActionZoomButtonGroupType,
            ActionButtonType.NoParentActionSelectionButtonGroupType,
            -> {}

            ActionButtonType.InkBucketActionButtonType,
            ActionButtonType.InkEraserActionButtonType,
            ActionButtonType.InkPenActionButtonType,
            ActionButtonType.OpenToolsActionButtonType,
            ActionButtonType.UndoActionButtonType,
            ActionButtonType.RedoActionButtonType,
            ActionButtonType.ZoomInActionButtonType,
            ActionButtonType.ZoomOutActionButtonType,
            ActionButtonType.OpenPaletteActionButtonType,
            ActionButtonType.EditPaletteActionButtonType,
            ActionButtonType.SavePixelImageActionButtonType,
            ActionButtonType.OpenPixelImageActionButtonType,
            ActionButtonType.ExportPixelImageActionButtonType,
            ActionButtonType.MoveImageActionButtonType,
            ActionButtonType.MoveImageDownActionButtonType,
            ActionButtonType.MoveImageLeftActionButtonType,
            ActionButtonType.MoveImageRightActionButtonType,
            ActionButtonType.MoveImageUpActionButtonType,
            ActionButtonType.TogglePreviewActionButtonType,
            ActionButtonType.SelectRectangleActionButtonType,
            ActionButtonType.SelectTouchActionButtonType,
            ActionButtonType.SelectMagicWandActionButtonType,
            ActionButtonType.DeselectActionButtonType,
            ActionButtonType.MoveSelectionActionButtonType,
            -> {
                ActionIconButton(
                    modifier,
                    actionIconButtonType = actionButtonType as ActionIconButtonType,
                    size,
                    isEnabled,
                    onClick,
                )
            }

            is ActionButtonType.PickPaletteColorActionButtonType -> ActionPickPaletteColorButton(
                modifier,
                pickPaletteColorActionButtonType = actionButtonType,
                size,
                isEnabled,
                onClick,
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    modifier: Modifier = Modifier,
    actionIconButtonType: ActionIconButtonType,
    size: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        modifier,
        drawableResId = actionIconButtonType.icon,
        contentDescriptionResId = actionIconButtonType.contentDescription,
        size,
        isEnabled,
        onClick,
    )
}

@Composable
private fun ActionPickPaletteColorButton(
    modifier: Modifier = Modifier,
    pickPaletteColorActionButtonType: ActionButtonType.PickPaletteColorActionButtonType,
    size: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val paletteIndex = pickPaletteColorActionButtonType.paletteIndex
    val color = Color.fromColorLong(pickPaletteColorActionButtonType.palette.colors[paletteIndex])
    val contentDescriptionValue = stringResource(
        R.string.cd_actions_section_button_pick_palette_color,
        paletteIndex,
    )

    PaletteColorButton(
        modifier,
        color,
        size,
        isEnabled,
        contentDescriptionValue,
        onClick,
    )
}

@Preview(showBackground = true)
@Composable
fun IconButtonEnabledPreview() {
    SpritedTheme {
        ActionButton(
            actionButtonType = ActionButtonType.RedoActionButtonType,
            size = 48.dp,
            isEnabled = true,
            isSelected = false,
        ) {
            android.util.Log.d("ActionIconButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonSelectedPreview() {
    SpritedTheme {
        ActionButton(
            actionButtonType = ActionButtonType.RedoActionButtonType,
            size = 48.dp,
            isEnabled = true,
            isSelected = true,
        ) {
            android.util.Log.d("ActionIconButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonDisabledPreview() {
    SpritedTheme {
        SpritedTheme {
            ActionButton(
                actionButtonType = ActionButtonType.RedoActionButtonType,
                size = 48.dp,
                isEnabled = false,
                isSelected = false,
            ) {
                android.util.Log.d("ActionIconButton", "clicked!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionPickPaletteColorButtonPreview() {
    SpritedTheme {
        ActionButton(
            actionButtonType = ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 0,
                palette = PaletteModel(
                    colors = listOf(
                        Color.Blue.toColorLong(),
                        Color.Red.toColorLong(),
                    ),
                ),
            ),
            size = 48.dp,
            isEnabled = true,
            isSelected = false,
        ) {
            android.util.Log.d("ActionPickPaletteColorButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionPickPaletteColorButtonSelectedPreview() {
    SpritedTheme {
        ActionButton(
            actionButtonType = ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 0,
                palette = PaletteModel(
                    colors = listOf(
                        Color.Blue.toColorLong(),
                        Color.Red.toColorLong(),
                    ),
                ),
            ),
            size = 48.dp,
            isEnabled = true,
            isSelected = true,
        ) {
            android.util.Log.d("ActionPickPaletteColorButton", "clicked!")
        }
    }
}
