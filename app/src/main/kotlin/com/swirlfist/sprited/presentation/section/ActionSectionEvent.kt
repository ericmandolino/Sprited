package com.swirlfist.sprited.presentation.section

sealed interface ActionSectionEvent {

    data object NoActionSectionEvent : ActionSectionEvent

    data object MoveImageActionButtonClicked : ActionSectionEvent

    data object TogglePreviewButtonClicked : ActionSectionEvent

    data object DeselectActionButtonClicked : ActionSectionEvent

    data object MoveSelectionActionButtonClicked : ActionSectionEvent
}

sealed interface FileButtonClicked : ActionSectionEvent {
    data object SavePixelImageButtonClicked : FileButtonClicked

    data object OpenPixelImageButtonClicked : FileButtonClicked

    data object ExportPixelImageButtonClicked : FileButtonClicked
}

sealed interface MoveImageDirectionButtonClicked : ActionSectionEvent {
    data object MoveImageDownButtonClicked : MoveImageDirectionButtonClicked

    data object MoveImageLeftButtonClicked : MoveImageDirectionButtonClicked

    data object MoveImageRightButtonClicked : MoveImageDirectionButtonClicked

    data object MoveImageUpButtonClicked : MoveImageDirectionButtonClicked
}

sealed interface PaletteButtonClicked : ActionSectionEvent {
    data object OpenPaletteButtonClicked : PaletteButtonClicked

    data object EditPaletteButtonClicked : PaletteButtonClicked

    data class PickPaletteColorButtonClicked(
        val pickPaletteColorActionButtonType: ActionButtonType.PickPaletteColorActionButtonType,
    ) : PaletteButtonClicked

    data object InkEraserButtonClicked : PaletteButtonClicked
}

sealed interface UndoRedoButtonClicked : ActionSectionEvent {
    data object UndoButtonClicked : UndoRedoButtonClicked

    data object RedoButtonClicked : UndoRedoButtonClicked
}

sealed interface SelectionButtonClicked : ActionSectionEvent {
    data object SelectRectangleActionButtonClicked : SelectionButtonClicked

    data object SelectTouchActionButtonClicked : SelectionButtonClicked

    data object SelectMagicWandActionButtonClicked : SelectionButtonClicked
}

sealed interface ToolButtonClicked : ActionSectionEvent {
    data object OpenToolsButtonClicked : ToolButtonClicked

    data object InkBucketButtonClicked : ToolButtonClicked

    data object InkPenButtonClicked : ToolButtonClicked
}

sealed interface ZoomButtonClicked : ActionSectionEvent {
    data object ZoomInButtonClicked : ZoomButtonClicked

    data object ZoomOutButtonClicked : ZoomButtonClicked
}
