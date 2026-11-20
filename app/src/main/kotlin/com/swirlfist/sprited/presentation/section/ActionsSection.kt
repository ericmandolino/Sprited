package com.swirlfist.sprited.presentation.section

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.swirlfist.sprited.domain.model.ActionModel
import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.presentation.state.ActionsSectionState
import com.swirlfist.sprited.presentation.theme.SpritedTheme
import com.swirlfist.sprited.presentation.uielements.ActionButton
import com.swirlfist.sprited.presentation.uielements.ActionButtonState
import kotlin.collections.listOf

private const val MAX_BUTTON_GROUP_RENDER = 5
private const val BUTTON_SIZE_DP = 48

@Composable
fun ActionsSection(
    modifier: Modifier = Modifier,
    state: ActionsSectionState,
    onEvent: (ActionSectionEvent) -> Unit,
) {
    val actionModels = state.actionModels.values

    FlowRow(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        actionModels.forEach { actionModel ->
            when (actionModel) {
                is ActionModel.ButtonActionModel,
                -> ButtonAction(
                    buttonActionModel = actionModel,
                    onEvent = onEvent,
                )

                is ActionModel.ButtonGroupActionModel,
                -> ButtonGroupAction(
                    actionType = actionModel.actionType,
                    isEnabled = actionModel.isEnabled,
                    childButtonActionModels = actionModel.childButtonActionModels,
                    isSelectable = false,
                    onEvent,
                )

                is ActionModel.SelectableButtonGroupActionModel,
                -> ButtonGroupAction(
                    actionType = actionModel.actionType,
                    isEnabled = actionModel.isEnabled,
                    childButtonActionModels = actionModel.childButtonActionModels,
                    isSelectable = true,
                    onEvent,
                )
            }
        }
    }
}

@Composable
private fun ButtonAction(
    buttonActionModel: ActionModel.ButtonActionModel,
    onEvent: (ActionSectionEvent) -> Unit,
) {
    ActionButton(
        state = ActionButtonState(
            actionButtonType = buttonActionModel.actionType,
            size = BUTTON_SIZE_DP.dp,
            isEnabled = buttonActionModel.isEnabled,
            isSelected = buttonActionModel.isSelected,
            onClick = { onEvent(buttonActionModel.actionType.toActionsSectionEvent()) },
        ),
    )
}

@Composable
private fun ButtonGroupAction(
    actionType: ActionButtonType,
    isEnabled: Boolean,
    childButtonActionModels: List<ActionModel.ButtonActionModel>,
    isSelectable: Boolean,
    onEvent: (ActionSectionEvent) -> Unit,
) {
    if (actionType is NoParentActionButtonGroupType ||
        (isEnabled && childButtonActionModels.size in 1..MAX_BUTTON_GROUP_RENDER)
    ) {
        ButtonGroupActionComponent(
            childButtonActionModels,
            onEvent,
        )
    } else if (isSelectable) {
        val selected = childButtonActionModels.find { actionModel ->
            actionModel.isSelected
        }
        if (selected != null) {
            ButtonGroupActionComponent(
                childButtonActionModels = listOf(
                    ActionModel.ButtonActionModel(
                        actionType,
                        isEnabled,
                    ),
                    selected,
                ),
                onEvent,
            )
        } else {
            ButtonAction(
                buttonActionModel = ActionModel.ButtonActionModel(
                    actionType,
                    isEnabled,
                ),
                onEvent = onEvent,
            )
        }
    } else {
        ButtonAction(
            buttonActionModel = ActionModel.ButtonActionModel(
                actionType,
                isEnabled,
            ),
            onEvent = onEvent,
        )
    }
}

@Composable
private fun ButtonGroupActionComponent(
    childButtonActionModels: List<ActionModel.ButtonActionModel>,
    onEvent: (ActionSectionEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = IconButtonDefaults.filledIconButtonColors().containerColor,
                ),
                shape = RoundedCornerShape(4.dp),
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        childButtonActionModels.forEach { buttonActionModel ->
            ButtonAction(
                buttonActionModel,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
fun SelectableButtonGroupDialog(
    childButtonActionModels: List<ActionModel.ButtonActionModel>,
    onEvent: (ActionSectionEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                childButtonActionModels.forEach { buttonActionModel ->
                    ButtonAction(
                        buttonActionModel,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ButtonGroupActionOverflowPreview() {
    val selectableButtonGroupActionModel = ActionModel.ButtonGroupActionModel(
        actionType = ActionButtonType.OpenPaletteActionButtonType,
        childButtonActionModels = createOverflowButtonGroupChildren(),
    )

    SpritedTheme {
        ButtonGroupAction(
            actionType = selectableButtonGroupActionModel.actionType,
            isEnabled = true,
            childButtonActionModels = selectableButtonGroupActionModel.childButtonActionModels,
            isSelectable = false,
            onEvent = {},
        )
    }
}

@Preview
@Composable
fun ButtonGroupActionSelectableOverflowPreview() {
    val selectableButtonGroupActionModel = ActionModel.SelectableButtonGroupActionModel(
        actionType = ActionButtonType.OpenPaletteActionButtonType,
        childButtonActionModels = createOverflowButtonGroupChildren(),
    )

    SpritedTheme {
        ButtonGroupAction(
            actionType = selectableButtonGroupActionModel.actionType,
            isEnabled = true,
            childButtonActionModels = selectableButtonGroupActionModel.childButtonActionModels,
            isSelectable = true,
            onEvent = {},
        )
    }
}

private fun createOverflowButtonGroupChildren(): List<ActionModel.ButtonActionModel> {
    val palette = PaletteModel(
        colors = listOf(
            Color.Black.toColorLong(),
            Color.White.toColorLong(),
            Color.Yellow.toColorLong(),
            Color.Red.toColorLong(),
            Color.Blue.toColorLong(),
            Color.Green.toColorLong(),
        ),
    )

    return listOf(
        ActionModel.ButtonActionModel(
            ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 0,
                palette = palette,
            ),
        ),
        ActionModel.ButtonActionModel(
            ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 1,
                palette = palette,
            ),
        ),
        ActionModel.ButtonActionModel(
            ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 2,
                palette = palette,
            ),
            isSelected = true,
        ),
        ActionModel.ButtonActionModel(
            ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 3,
                palette = palette,
            ),
        ),
        ActionModel.ButtonActionModel(
            ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 4,
                palette = palette,
            ),
        ),
        ActionModel.ButtonActionModel(
            ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 5,
                palette = palette,
            ),
        ),
    )
}

@Suppress("LongMethod")
@Preview(name = "no-scroll-actions-section", showBackground = true, widthDp = 320, heightDp = 320)
@Preview(name = "scroll-actions-section", showBackground = true, widthDp = 200, heightDp = 120)
@Composable
fun ActionsSectionPreview() {
    val palette = PaletteModel(
        colors = listOf(
            Color.Black.toColorLong(),
            Color.Yellow.toColorLong(),
            Color.Red.toColorLong(),
            Color.Blue.toColorLong(),
            Color.Green.toColorLong(),
        ),
    )
    SpritedTheme {
        ActionsSection(
            modifier = Modifier.fillMaxSize(),
            state = ActionsSectionState().copy(
                actionModels = mapOf(
                    ActionButtonType.OpenPaletteActionButtonType to
                        ActionModel.SelectableButtonGroupActionModel(
                            actionType = ActionButtonType.OpenPaletteActionButtonType,
                            isEnabled = true,
                            childButtonActionModels = listOf(
                                ActionModel.ButtonActionModel(
                                    ActionButtonType.PickPaletteColorActionButtonType(
                                        paletteIndex = 0,
                                        palette = palette,
                                    ),
                                ),
                                ActionModel.ButtonActionModel(
                                    ActionButtonType.PickPaletteColorActionButtonType(
                                        paletteIndex = 1,
                                        palette = palette,
                                    ),
                                ),
                                ActionModel.ButtonActionModel(
                                    ActionButtonType.PickPaletteColorActionButtonType(
                                        paletteIndex = 2,
                                        palette = palette,
                                    ),
                                ),
                                ActionModel.ButtonActionModel(
                                    ActionButtonType.PickPaletteColorActionButtonType(
                                        paletteIndex = 3,
                                        palette = palette,
                                    ),
                                ),
                            ),
                        ),
                    ActionButtonType.UndoActionButtonType to ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.UndoActionButtonType,
                        isEnabled = true,
                    ),
                    ActionButtonType.RedoActionButtonType to ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.RedoActionButtonType,
                        isEnabled = false,
                    ),
                    ActionButtonType.ZoomInActionButtonType to ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.ZoomInActionButtonType,
                        isEnabled = true,
                    ),
                    ActionButtonType.ZoomOutActionButtonType to ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.ZoomOutActionButtonType,
                        isEnabled = true,
                    ),
                    ActionButtonType.SavePixelImageActionButtonType to
                        ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.SavePixelImageActionButtonType,
                            isEnabled = true,
                        ),
                    ActionButtonType.OpenPixelImageActionButtonType to
                        ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.OpenPixelImageActionButtonType,
                            isEnabled = true,
                        ),
                ),
            ),
        ) { actionType ->
            Log.d("ActionsSection", "action: $actionType")
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun SelectableButtonGroupDialogPreview() {
    SpritedTheme {
        SelectableButtonGroupDialog(
            childButtonActionModels = createOverflowButtonGroupChildren(),
            onEvent = {},
            onDismiss = {},
        )
    }
}

@Suppress("CyclomaticComplexMethod")
fun ActionButtonType.toActionsSectionEvent(): ActionSectionEvent = when (this) {
    ActionButtonType.NoParentActionUndoRedoButtonGroupType,
    ActionButtonType.NoParentActionZoomButtonGroupType,
    ActionButtonType.NoParentActionSelectionButtonGroupType,
    -> ActionSectionEvent.NoActionSectionEvent

    ActionButtonType.InkBucketActionButtonType ->
        ToolButtonClicked.InkBucketButtonClicked
    ActionButtonType.InkEraserActionButtonType ->
        PaletteButtonClicked.InkEraserButtonClicked
    ActionButtonType.InkPenActionButtonType ->
        ToolButtonClicked.InkPenButtonClicked
    ActionButtonType.MoveImageActionButtonType ->
        ActionSectionEvent.MoveImageActionButtonClicked
    ActionButtonType.MoveImageDownActionButtonType ->
        MoveImageDirectionButtonClicked.MoveImageDownButtonClicked
    ActionButtonType.MoveImageLeftActionButtonType ->
        MoveImageDirectionButtonClicked.MoveImageLeftButtonClicked
    ActionButtonType.MoveImageRightActionButtonType ->
        MoveImageDirectionButtonClicked.MoveImageRightButtonClicked
    ActionButtonType.MoveImageUpActionButtonType ->
        MoveImageDirectionButtonClicked.MoveImageUpButtonClicked
    ActionButtonType.OpenToolsActionButtonType ->
        ToolButtonClicked.OpenToolsButtonClicked
    ActionButtonType.UndoActionButtonType ->
        UndoRedoButtonClicked.UndoButtonClicked
    ActionButtonType.RedoActionButtonType ->
        UndoRedoButtonClicked.RedoButtonClicked
    ActionButtonType.ZoomInActionButtonType ->
        ZoomButtonClicked.ZoomInButtonClicked
    ActionButtonType.ZoomOutActionButtonType ->
        ZoomButtonClicked.ZoomOutButtonClicked
    ActionButtonType.OpenPaletteActionButtonType ->
        PaletteButtonClicked.OpenPaletteButtonClicked
    ActionButtonType.EditPaletteActionButtonType ->
        PaletteButtonClicked.EditPaletteButtonClicked
    is ActionButtonType.PickPaletteColorActionButtonType ->
        PaletteButtonClicked.PickPaletteColorButtonClicked(
            pickPaletteColorActionButtonType = this,
        )

    ActionButtonType.SavePixelImageActionButtonType ->
        FileButtonClicked.SavePixelImageButtonClicked
    ActionButtonType.OpenPixelImageActionButtonType ->
        FileButtonClicked.OpenPixelImageButtonClicked
    ActionButtonType.ExportPixelImageActionButtonType ->
        FileButtonClicked.ExportPixelImageButtonClicked
    ActionButtonType.TogglePreviewActionButtonType ->
        ActionSectionEvent.TogglePreviewButtonClicked
    ActionButtonType.SelectRectangleActionButtonType ->
        SelectionButtonClicked.SelectRectangleActionButtonClicked
    ActionButtonType.SelectTouchActionButtonType ->
        SelectionButtonClicked.SelectTouchActionButtonClicked
    ActionButtonType.SelectMagicWandActionButtonType ->
        SelectionButtonClicked.SelectMagicWandActionButtonClicked
    ActionButtonType.DeselectActionButtonType ->
        ActionSectionEvent.DeselectActionButtonClicked
    ActionButtonType.MoveSelectionActionButtonType ->
        ActionSectionEvent.MoveSelectionActionButtonClicked
}
