@file:Suppress("TooManyFunctions")

package com.swirlfist.sprited.presentation.screen

import com.swirlfist.sprited.domain.model.ActionModel
import com.swirlfist.sprited.domain.model.BaseButtonGroupActionModel
import com.swirlfist.sprited.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.sprited.domain.model.FloatingPixelImageModel
import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.domain.model.PixelSelectionModel
import com.swirlfist.sprited.presentation.createPaletteButtons
import com.swirlfist.sprited.presentation.isSelected
import com.swirlfist.sprited.presentation.section.ActionButtonType
import com.swirlfist.sprited.presentation.state.ActionsSectionState
import com.swirlfist.sprited.presentation.state.MainScreenState
import com.swirlfist.sprited.presentation.uielements.MAX_ZOOM_FACTOR
import com.swirlfist.sprited.presentation.uielements.MIN_ZOOM_FACTOR
import kotlin.collections.forEach
import kotlin.collections.set

const val ERASER_TOOL_PALETTE_INDEX = EMPTY_PIXEL_PALETTE_INDEX

internal fun MainScreenState.updateSelection(
    pixelSelection: PixelSelectionModel?,
    floatingPixelImage: FloatingPixelImageModel?,
): MainScreenState = copy(
    canvasSectionState = canvasSectionState.copy(
        pixelSelectionModel = pixelSelection,
        floatingPixelImageModel = floatingPixelImage,
    ),
    actionsSectionState = actionsSectionState.updateSelectionRelatedButtonsState(
        pixelSelection,
        floatingPixelImage,
    ),
)

internal fun ActionsSectionState.updateZoomButtonState(zoomFactor: Float): ActionsSectionState =
    updateButtonEnabled(
        ActionButtonType.ZoomInActionButtonType,
        isEnabled = zoomFactor < MAX_ZOOM_FACTOR,
    ).updateButtonEnabled(
        ActionButtonType.ZoomOutActionButtonType,
        isEnabled = zoomFactor > MIN_ZOOM_FACTOR,
    )

internal fun ActionsSectionState.updateUndoButtonState(isEnabled: Boolean): ActionsSectionState =
    updateButtonEnabled(
        ActionButtonType.UndoActionButtonType,
        isEnabled = isEnabled,
    )

internal fun ActionsSectionState.updateRedoButtonState(isEnabled: Boolean): ActionsSectionState =
    updateButtonEnabled(
        ActionButtonType.RedoActionButtonType,
        isEnabled = isEnabled,
    )

internal fun ActionsSectionState.updateSelectionRelatedButtonsState(
    pixelSelection: PixelSelectionModel?,
    floatingPixelImage: FloatingPixelImageModel?,
): ActionsSectionState {
    val isAnySelectionActive =
        pixelSelection != null &&
            (
                pixelSelection.rectangleSelection != null ||
                    pixelSelection.selected.find { row ->
                        row.find { pixel ->
                            pixel != null
                        } != null
                    } != null
                )
    val isAnyFloatingPixelImageActive = floatingPixelImage != null

    val isDeselectButtonEnabled = isAnySelectionActive || isAnyFloatingPixelImageActive
    val isMoveSelectionButtonEnabled = isAnySelectionActive || isAnyFloatingPixelImageActive
    val isMoveSelectionButtonSelected = isSelected(ActionButtonType.MoveSelectionActionButtonType)
    val isMoveSelectionButtonToBeToggled = if (isAnyFloatingPixelImageActive) {
        !isMoveSelectionButtonSelected
    } else {
        isMoveSelectionButtonSelected
    }

    return updateButtonEnabled(
        ActionButtonType.DeselectActionButtonType,
        isEnabled = isDeselectButtonEnabled,
    ).updateButtonEnabled(
        ActionButtonType.MoveSelectionActionButtonType,
        isEnabled = isMoveSelectionButtonEnabled,
    ).let { updatedState ->
        if (isMoveSelectionButtonToBeToggled) {
            updatedState.toggleMoveSelection()
        } else {
            updatedState
        }
    }
}

internal fun ActionsSectionState.toggleMoveSelection(): ActionsSectionState =
    toggleSelectableButton(
        ActionButtonType.MoveSelectionActionButtonType,
    ).let { updatedSectionState ->
        if (updatedSectionState.isSelected(ActionButtonType.MoveSelectionActionButtonType)) {
            updatedSectionState.onMoveSelectionSelected()
        } else {
            updatedSectionState.onMoveSelectionDeselected()
        }
    }

internal fun ActionsSectionState.onMoveSelectionSelected() = this
    .disablePaintButtons()
    .disableSelectionButtons()

internal fun ActionsSectionState.onMoveSelectionDeselected() = enableSelectionButtons().apply {
    if (!isAnySelectToolSelected()) {
        enablePaintButtons()
    }
}

internal fun ActionsSectionState.updateButtonEnabled(
    actionButtonType: ActionButtonType,
    isEnabled: Boolean,
): ActionsSectionState {
    val buttonModel = actionModels[actionButtonType]

    return if (buttonModel != null) {
        if (buttonModel.isEnabled == isEnabled) {
            this
        } else {
            val updatedModel = when (buttonModel) {
                is ActionModel.ButtonActionModel -> buttonModel.copy(isEnabled = isEnabled)
                is ActionModel.ButtonGroupActionModel -> buttonModel.copy(isEnabled = isEnabled)
                is ActionModel.SelectableButtonGroupActionModel -> buttonModel.copy(
                    isEnabled = isEnabled,
                )
            }
            copy(
                actionModels = actionModels.toMutableMap().also { actionModels ->
                    actionModels[actionButtonType] = updatedModel
                },
            )
        }
    } else {
        updateChildButtonEnabled(
            actionButtonType,
            isEnabled,
        )
    }
}

internal fun ActionsSectionState.updateChildButtonEnabled(
    actionButtonType: ActionButtonType,
    isEnabled: Boolean,
): ActionsSectionState {
    val buttonGroups = actionModels.values.filterIsInstance<BaseButtonGroupActionModel>()
    if (buttonGroups.isEmpty()) {
        return this
    }

    return buttonGroups.findChildActionModel(actionButtonType)?.let { (buttonGroup, childIndex) ->
        val updatedChildButtons = buttonGroup.childButtonActionModels.toMutableList().apply {
            val childButton = get(childIndex)
            set(childIndex, childButton.copy(isEnabled = isEnabled))
        }
        val updatedButtonGroup = when (buttonGroup) {
            is ActionModel.ButtonGroupActionModel -> buttonGroup.copy(
                childButtonActionModels = updatedChildButtons,
            )
            is ActionModel.SelectableButtonGroupActionModel -> buttonGroup.copy(
                childButtonActionModels = updatedChildButtons,
            )
        }
        copy(
            actionModels = actionModels.toMutableMap().also { actionModels ->
                actionModels[buttonGroup.actionType] = updatedButtonGroup
            },
        )
    } ?: this
}

internal fun Collection<BaseButtonGroupActionModel>.findChildActionModel(
    actionButtonType: ActionButtonType,
): Pair<BaseButtonGroupActionModel, Int>? {
    forEach { buttonGroupActionModel ->
        buttonGroupActionModel.childButtonActionModels.forEachIndexed {
                index,
                childButtonActionModel,
            ->
            if (childButtonActionModel.actionType == actionButtonType) {
                return Pair(buttonGroupActionModel, index)
            }
        }
    }

    return null
}

internal fun ActionsSectionState.toggleSelectableButton(
    actionButtonType: ActionButtonType,
): ActionsSectionState {
    val actionModel = actionModels[actionButtonType] ?: return this

    return when (actionModel) {
        is ActionModel.ButtonActionModel -> copy(
            actionModels = actionModels.toMutableMap().apply {
                put(
                    actionModel.actionType,
                    actionModel.copy(
                        isSelected = !actionModel.isSelected,
                    ),
                )
            },
        )

        is ActionModel.ButtonGroupActionModel,
        is ActionModel.SelectableButtonGroupActionModel,
        -> this
    }
}

internal fun ActionsSectionState.updateSelectedChildButton(
    actionButtonType: ActionButtonType,
    isToggle: Boolean = false,
): ActionsSectionState {
    val parentActionModel = actionModels.values.find { actionModel ->
        actionModel is ActionModel.SelectableButtonGroupActionModel &&
            actionModel.childButtonActionModels.find { childButtonActionModel ->
                childButtonActionModel.actionType == actionButtonType
            } != null
    } ?: return this

    return when (parentActionModel) {
        is ActionModel.ButtonActionModel,
        is ActionModel.ButtonGroupActionModel,
        -> this

        is ActionModel.SelectableButtonGroupActionModel -> {
            copy(
                actionModels = actionModels.toMutableMap().apply {
                    put(
                        parentActionModel.actionType,
                        parentActionModel.copy(
                            childButtonActionModels = parentActionModel.childButtonActionModels
                                .map { child ->
                                    val isSelected = if (isToggle) {
                                        (child.actionType == actionButtonType) && !child.isSelected
                                    } else {
                                        child.actionType == actionButtonType
                                    }
                                    child.copy(
                                        isSelected = isSelected,
                                    )
                                },
                        ),
                    )
                },
            )
        }
    }
}

internal fun ActionsSectionState.enablePaintButtons() = setPaintButtonsEnabled(true)

internal fun ActionsSectionState.disablePaintButtons() = setPaintButtonsEnabled(false)

internal fun ActionsSectionState.setPaintButtonsEnabled(isEnabled: Boolean): ActionsSectionState {
    val openToolsButtonGroup = actionModels[ActionButtonType.OpenToolsActionButtonType]
        as ActionModel.SelectableButtonGroupActionModel
    val childButtons = openToolsButtonGroup.childButtonActionModels

    return copy(
        actionModels = actionModels.toMutableMap().apply {
            put(
                ActionButtonType.OpenToolsActionButtonType,
                openToolsButtonGroup.copy(
                    childButtonActionModels = childButtons.map { childButton ->
                        childButton.copy(
                            isEnabled = isEnabled,
                        )
                    },
                ),
            )
        },
    )
}

internal fun ActionsSectionState.enableSelectionButtons() = setSelectionButtonsEnabled(true)

internal fun ActionsSectionState.disableSelectionButtons() = setSelectionButtonsEnabled(false)

internal fun ActionsSectionState.setSelectionButtonsEnabled(
    isEnabled: Boolean,
): ActionsSectionState {
    val selectionButtonGroup = actionModels[ActionButtonType.NoParentActionSelectionButtonGroupType]
        as ActionModel.SelectableButtonGroupActionModel
    val childButtons = selectionButtonGroup.childButtonActionModels

    return copy(
        actionModels = actionModels.toMutableMap().apply {
            put(
                ActionButtonType.NoParentActionSelectionButtonGroupType,
                selectionButtonGroup.copy(
                    isEnabled = isEnabled,
                    childButtonActionModels = childButtons.map { childButton ->
                        childButton.copy(
                            isEnabled = isEnabled,
                        )
                    },
                ),
            )
        },
    )
}

internal fun ActionsSectionState.updatePaletteButtons(palette: PaletteModel): ActionsSectionState {
    val openPaletteActionModel =
        actionModels[ActionButtonType.OpenPaletteActionButtonType]
            as ActionModel.SelectableButtonGroupActionModel

    return copy(
        actionModels = actionModels.toMutableMap().apply {
            put(
                ActionButtonType.OpenPaletteActionButtonType,
                openPaletteActionModel.copy(
                    childButtonActionModels = palette.createPaletteButtons(),
                ),
            )
        },
    )
}

internal fun MainScreenState.getPaletteIndex(): Int = if (isEraserSelected()) {
    ERASER_TOOL_PALETTE_INDEX
} else {
    actionsSectionState.getPaletteIndex()
}

internal fun MainScreenState.isEraserSelected() = actionsSectionState.isSelected(
    ActionButtonType.InkEraserActionButtonType,
)

internal fun MainScreenState.isSelectRectSelected() = actionsSectionState.isSelectRectSelected()

internal fun MainScreenState.isSelectTouchSelected() = actionsSectionState.isSelectTouchSelected()

internal fun MainScreenState.isSelectMagicWandSelected() =
    actionsSectionState.isSelectMagicWandSelected()

internal fun MainScreenState.isMoveSelectionSelected() = actionsSectionState.isSelected(
    ActionButtonType.MoveSelectionActionButtonType,
)

internal fun ActionsSectionState.isAnySelectToolSelected() =
    isSelectRectSelected() || isSelectTouchSelected() || isSelectMagicWandSelected()

internal fun ActionsSectionState.isSelectRectSelected() = isSelected(
    ActionButtonType.SelectRectangleActionButtonType,
)

internal fun ActionsSectionState.isSelectTouchSelected() = isSelected(
    ActionButtonType.SelectTouchActionButtonType,
)

internal fun ActionsSectionState.isSelectMagicWandSelected() = isSelected(
    ActionButtonType.SelectMagicWandActionButtonType,
)

internal fun MainScreenState.getSelectedPaintTool() = actionsSectionState.getSelectedPaintTool()

internal fun ActionsSectionState.getSelectedPaintTool(): ActionButtonType = getSelectedChildButton(
    buttonGroup = ActionButtonType.OpenToolsActionButtonType,
)

internal fun ActionsSectionState.getPaletteIndex(): Int = (
    getSelectedChildButton(
        buttonGroup = ActionButtonType.OpenPaletteActionButtonType,
    ) as ActionButtonType.PickPaletteColorActionButtonType
    ).paletteIndex

internal fun ActionsSectionState.getSelectedChildButton(buttonGroup: ActionButtonType) =
    (actionModels[buttonGroup] as ActionModel.SelectableButtonGroupActionModel)
        .childButtonActionModels.first { child ->
            child.isSelected
        }.actionType
