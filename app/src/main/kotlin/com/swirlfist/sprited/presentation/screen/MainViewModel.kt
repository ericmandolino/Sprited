package com.swirlfist.sprited.presentation.screen

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.sprited.domain.error.ExportPixelImageError
import com.swirlfist.sprited.domain.error.OpenPixelImageError
import com.swirlfist.sprited.domain.getPixelHeight
import com.swirlfist.sprited.domain.model.ActionModel
import com.swirlfist.sprited.domain.model.FloatingPixelImageModel
import com.swirlfist.sprited.domain.model.PaletteModel
import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelMatrixModel
import com.swirlfist.sprited.domain.model.PixelSelectionModel
import com.swirlfist.sprited.domain.usecase.ApplyBucketUseCase
import com.swirlfist.sprited.domain.usecase.ClearEditorActionsUseCase
import com.swirlfist.sprited.domain.usecase.ConvertSelectionToFloatingImageUseCase
import com.swirlfist.sprited.domain.usecase.DeselectUseCase
import com.swirlfist.sprited.domain.usecase.ExportPixelImageToPngUseCase
import com.swirlfist.sprited.domain.usecase.ExportPixelImageToSvgUseCase
import com.swirlfist.sprited.domain.usecase.GetBasePixelImageUseCase
import com.swirlfist.sprited.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.sprited.domain.usecase.GetRectangleSelectionActionTypeUseCase
import com.swirlfist.sprited.domain.usecase.GetRedoEditorActionAvailableUseCase
import com.swirlfist.sprited.domain.usecase.GetUndoEditorActionAvailableUseCase
import com.swirlfist.sprited.domain.usecase.LandFloatingImageUseCase
import com.swirlfist.sprited.domain.usecase.MoveDirection
import com.swirlfist.sprited.domain.usecase.MoveFloatingImageUseCase
import com.swirlfist.sprited.domain.usecase.MoveImageUseCase
import com.swirlfist.sprited.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.sprited.domain.usecase.RectangleSelectionActionType
import com.swirlfist.sprited.domain.usecase.RedoEditorActionUseCase
import com.swirlfist.sprited.domain.usecase.SavePixelImageUseCase
import com.swirlfist.sprited.domain.usecase.SelectWithMagicWandUseCase
import com.swirlfist.sprited.domain.usecase.TogglePixelSelectionUseCase
import com.swirlfist.sprited.domain.usecase.UndoEditorActionUseCase
import com.swirlfist.sprited.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.sprited.domain.usecase.UpdateRectangleSelectionUseCase
import com.swirlfist.sprited.domain.usecase.UseCaseParams
import com.swirlfist.sprited.domain.usecase.execute
import com.swirlfist.sprited.presentation.createEditorActionContext
import com.swirlfist.sprited.presentation.createPaletteButtons
import com.swirlfist.sprited.presentation.isSelected
import com.swirlfist.sprited.presentation.section.ActionButtonType
import com.swirlfist.sprited.presentation.section.ActionSectionEvent
import com.swirlfist.sprited.presentation.section.CanvasSectionEvent
import com.swirlfist.sprited.presentation.section.FileButtonClicked
import com.swirlfist.sprited.presentation.section.MoveImageDirectionButtonClicked
import com.swirlfist.sprited.presentation.section.PaletteButtonClicked
import com.swirlfist.sprited.presentation.section.SelectionButtonClicked
import com.swirlfist.sprited.presentation.section.ToolButtonClicked
import com.swirlfist.sprited.presentation.section.UndoRedoButtonClicked
import com.swirlfist.sprited.presentation.section.ZoomButtonClicked
import com.swirlfist.sprited.presentation.state.ActionsSectionState
import com.swirlfist.sprited.presentation.state.CanvasSectionState
import com.swirlfist.sprited.presentation.state.MainScreenState
import com.swirlfist.sprited.presentation.state.PixelImageExportFormat
import com.swirlfist.sprited.presentation.state.PixelImagePreviewSectionState
import com.swirlfist.sprited.presentation.state.updateSelectedPreviewBackgroundColor
import com.swirlfist.sprited.presentation.uielements.MAX_ZOOM_FACTOR
import com.swirlfist.sprited.presentation.uielements.MIN_ZOOM_FACTOR
import com.swirlfist.sprited.presentation.updateWith
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val DEFAULT_ZOOM_FACTOR = 1F
private const val ONCE_MORE_TO_EXIT_DURATION_SECONDS = 4

@Suppress("LargeClass", "LongParameterList", "TooManyFunctions")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBasePixelImageUseCase: GetBasePixelImageUseCase,
    private val savePixelImageUseCase: SavePixelImageUseCase,
    private val exportPixelImageToSvgUseCase: ExportPixelImageToSvgUseCase,
    private val exportPixelImageToPngUseCase: ExportPixelImageToPngUseCase,
    private val openPixelImageUseCase: OpenPixelImageUseCase,
    private val getNextZoomFactorUseCase: GetNextZoomFactorUseCase,
    private val updatePixelColorUseCase: UpdatePixelColorUseCase,
    private val applyBucketUseCase: ApplyBucketUseCase,
    private val moveImageUseCase: MoveImageUseCase,
    private val getUndoEditorActionAvailableUseCase: GetUndoEditorActionAvailableUseCase,
    private val getRedoEditorActionAvailableUseCase: GetRedoEditorActionAvailableUseCase,
    private val undoEditorActionUseCase: UndoEditorActionUseCase,
    private val redoEditorActionUseCase: RedoEditorActionUseCase,
    private val clearEditorActionUseCase: ClearEditorActionsUseCase,
    private val togglePixelSelectionUseCase: TogglePixelSelectionUseCase,
    private val selectWithMagicWandUseCase: SelectWithMagicWandUseCase,
    private val getRectangleSelectionActionTypeUseCase: GetRectangleSelectionActionTypeUseCase,
    private val updateRectangleSelectionUseCase: UpdateRectangleSelectionUseCase,
    private val deselectUseCase: DeselectUseCase,
    private val convertSelectionToFloatingImageUseCase: ConvertSelectionToFloatingImageUseCase,
    private val landFloatingImageUseCase: LandFloatingImageUseCase,
    private val moveFloatingImageUseCase: MoveFloatingImageUseCase,
) : ViewModel() {
    private val _mainScreenState = MutableStateFlow(
        value = MainScreenState(
            canvasSectionState = CanvasSectionState(),
            actionsSectionState = ActionsSectionState(),
            pixelImagePreviewSectionState = PixelImagePreviewSectionState(
                onPreviewBackgroundColorSelected = ::onPreviewBackgroundColorSelected,
            ),
        ),
    )
    val mainScreenState = _mainScreenState.asStateFlow()

    init {
        viewModelScope.launch {
            val pixelImageModel = getBasePixelImageUseCase(UseCaseParams.NoParams).getOrNull()
                ?: PixelImageModel.createEmpty(
                    width = 24,
                    height = 24,
                    colors = listOf(Color.Black.toColorLong(), Color.White.toColorLong()),
                )

            _mainScreenState.update { mainScreenState ->
                val zoomFactor = DEFAULT_ZOOM_FACTOR
                mainScreenState.copy(
                    canvasSectionState = mainScreenState.canvasSectionState.copy(
                        pixelImageModel = pixelImageModel,
                        zoomFactor = zoomFactor,
                        isShowGridEnabled = true,
                    ),
                    actionsSectionState = mainScreenState.actionsSectionState.copy(
                        actionModels = createActions(pixelImageModel),
                    ),
                    pixelImagePreviewSectionState = mainScreenState.pixelImagePreviewSectionState
                        .copy(
                            pixelImageModel = pixelImageModel,
                            isFitAvailableSpace = true,
                        ),
                )
            }

            clearEditorActionUseCase(UseCaseParams.NoParams)

            launch {
                getUndoEditorActionAvailableUseCase(UseCaseParams.NoParams).getOrNull()
                    ?.collect { isUndoAvailable ->
                        _mainScreenState.update { mainScreenState ->
                            val actionsSectionState = mainScreenState.actionsSectionState
                            mainScreenState.copy(
                                actionsSectionState = actionsSectionState.updateUndoButtonState(
                                    isUndoAvailable,
                                ),
                            )
                        }
                    }
            }

            launch {
                getRedoEditorActionAvailableUseCase(UseCaseParams.NoParams).getOrNull()
                    ?.collect { isRedoAvailable ->
                        _mainScreenState.update { mainScreenState ->
                            val actionsSectionState = mainScreenState.actionsSectionState
                            mainScreenState.copy(
                                actionsSectionState = actionsSectionState.updateRedoButtonState(
                                    isRedoAvailable,
                                ),
                            )
                        }
                    }
            }
        }
    }

    fun disableBackHandlerTemporarily() {
        viewModelScope.launch {
            _mainScreenState.update { state ->
                state.copy(
                    isBackHandlerEnabled = false,
                )
            }
            delay(ONCE_MORE_TO_EXIT_DURATION_SECONDS.seconds)
            _mainScreenState.update { state ->
                state.copy(
                    isBackHandlerEnabled = true,
                )
            }
        }
    }

    fun onPixelImageExportSuccessDisplayed() {
        _mainScreenState.update { state ->
            state.copy(
                isShowPixelImageExportSuccess = false,
            )
        }
    }

    fun clearOpenPixelImageError() {
        _mainScreenState.update { state ->
            state.copy(
                openPixelImageError = null,
            )
        }
    }

    fun clearExportPixelImageError() {
        _mainScreenState.update { state ->
            state.copy(
                exportPixelImageError = null,
            )
        }
    }

    fun showPalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowPalette = true,
            )
        }
    }

    fun hidePalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowPalette = false,
            )
        }
    }

    fun onPreviewBackgroundColorSelected(color: Color) {
        _mainScreenState.update { state ->
            state.copy(
                pixelImagePreviewSectionState = state.pixelImagePreviewSectionState
                    .updateSelectedPreviewBackgroundColor(
                        color,
                    ),
            )
        }
    }

    fun showEditPalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowEditPalette = true,
            )
        }
    }

    fun hideEditPalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowEditPalette = false,
            )
        }
    }

    fun onEditPaletteChangesSaved(
        paletteModel: PaletteModel,
        pixelMatrixModel: PixelMatrixModel?,
    ) {
        hideEditPalette()

        if (pixelMatrixModel == null) {
            return
        }

        updatePixelImage(
            pixelImage = PixelImageModel(
                pixelMatrixModel,
                paletteModel,
            ),
            isPaletteUpdate = true,
        )
    }

    fun onCanvasSectionEvent(event: CanvasSectionEvent) {
        when (event) {
            is CanvasSectionEvent.PixelVisited -> onPixelVisited(event)
            is CanvasSectionEvent.PixelVisitCancel -> onPixelVisitCancel()
            is CanvasSectionEvent.PixelVisitEnd -> onPixelVisitFinish()
            is CanvasSectionEvent.ZoomUpdate -> {
                _mainScreenState.update { mainScreenState ->
                    val canvasSectionState = mainScreenState.canvasSectionState
                    val actionsSectionState = mainScreenState.actionsSectionState
                    mainScreenState.copy(
                        canvasSectionState = canvasSectionState.copy(
                            zoomFactor = event.zoom,
                        ),
                        actionsSectionState = actionsSectionState.updateZoomButtonState(
                            zoomFactor = event.zoom,
                        ),
                    )
                }
            }
        }
    }

    fun onActionsSectionEvent(event: ActionSectionEvent) {
        when (event) {
            ActionSectionEvent.NoActionSectionEvent -> {}

            is PaletteButtonClicked -> onPaletteButtonClicked(event)

            is ToolButtonClicked -> onToolButtonClicked(event)

            is UndoRedoButtonClicked -> onUndoRedoButtonClicked(event)

            is ZoomButtonClicked -> onZoomButtonClicked(event)

            is FileButtonClicked -> onFileButtonClicked(event)

            ActionSectionEvent.MoveImageActionButtonClicked -> {}

            is MoveImageDirectionButtonClicked -> onMoveImageButtonClicked(event)

            ActionSectionEvent.TogglePreviewButtonClicked -> togglePreview()

            is SelectionButtonClicked -> onSelectionButtonClicked(event)

            ActionSectionEvent.DeselectActionButtonClicked -> deselectPreviouslySelected()

            ActionSectionEvent.MoveSelectionActionButtonClicked -> toggleMoveSelection()
        }
    }

    fun onFileButtonClicked(event: FileButtonClicked) {
        when (event) {
            FileButtonClicked.SavePixelImageButtonClicked -> selectSavePixelImageLocation()

            FileButtonClicked.OpenPixelImageButtonClicked -> selectOpenPixelImageLocation()

            FileButtonClicked.ExportPixelImageButtonClicked -> selectExportPixelImageFormat()
        }
    }

    fun onMoveImageButtonClicked(event: MoveImageDirectionButtonClicked) {
        when (event) {
            MoveImageDirectionButtonClicked.MoveImageDownButtonClicked,
            -> moveImage(MoveDirection.DOWN)

            MoveImageDirectionButtonClicked.MoveImageLeftButtonClicked,
            -> moveImage(MoveDirection.LEFT)

            MoveImageDirectionButtonClicked.MoveImageRightButtonClicked,
            -> moveImage(MoveDirection.RIGHT)

            MoveImageDirectionButtonClicked.MoveImageUpButtonClicked,
            -> moveImage(MoveDirection.UP)
        }
    }

    fun onPaletteButtonClicked(event: PaletteButtonClicked) {
        when (event) {
            PaletteButtonClicked.OpenPaletteButtonClicked,
            -> showPalette()

            PaletteButtonClicked.EditPaletteButtonClicked,
            -> showEditPalette()

            is PaletteButtonClicked.PickPaletteColorButtonClicked,
            -> updateSelectedPaletteIndex(event.pickPaletteColorActionButtonType)

            PaletteButtonClicked.InkEraserButtonClicked,
            -> updateSelectedPaletteIndex(ActionButtonType.InkEraserActionButtonType)
        }
    }

    fun onSelectionButtonClicked(event: SelectionButtonClicked) {
        when (event) {
            SelectionButtonClicked.SelectRectangleActionButtonClicked,
            -> toggleSelectRectangle()

            SelectionButtonClicked.SelectTouchActionButtonClicked,
            -> toggleSelectTouch()

            SelectionButtonClicked.SelectMagicWandActionButtonClicked,
            -> toggleSelectMagicWand()
        }
    }

    fun onToolButtonClicked(event: ToolButtonClicked) {
        when (event) {
            ToolButtonClicked.InkBucketButtonClicked,
            -> updateSelectedTool(ActionButtonType.InkBucketActionButtonType)

            ToolButtonClicked.InkPenButtonClicked,
            -> updateSelectedTool(ActionButtonType.InkPenActionButtonType)

            ToolButtonClicked.OpenToolsButtonClicked -> {}
        }
    }

    fun onUndoRedoButtonClicked(event: UndoRedoButtonClicked) {
        when (event) {
            UndoRedoButtonClicked.RedoButtonClicked,
            -> redoEditorAction()

            UndoRedoButtonClicked.UndoButtonClicked,
            -> undoEditorAction()
        }
    }

    fun onZoomButtonClicked(event: ZoomButtonClicked) {
        when (event) {
            ZoomButtonClicked.ZoomInButtonClicked,
            -> zoom(isZoomIn = true)

            ZoomButtonClicked.ZoomOutButtonClicked,
            -> zoom(isZoomIn = false)
        }
    }

    @Suppress("LongMethod")
    private fun createActions(pixelImageModel: PixelImageModel) = mapOf(
        ActionButtonType.OpenPaletteActionButtonType to
            ActionModel.SelectableButtonGroupActionModel(
                actionType = ActionButtonType.OpenPaletteActionButtonType,
                childButtonActionModels = pixelImageModel.paletteModel.createPaletteButtons(),
            ),
        ActionButtonType.EditPaletteActionButtonType to ActionModel.ButtonActionModel(
            actionType = ActionButtonType.EditPaletteActionButtonType,
        ),
        ActionButtonType.OpenToolsActionButtonType to ActionModel.SelectableButtonGroupActionModel(
            actionType = ActionButtonType.OpenToolsActionButtonType,
            childButtonActionModels = listOf(
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.InkPenActionButtonType,
                    isSelected = true,
                ),
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.InkBucketActionButtonType,
                ),
            ),
        ),
        ActionButtonType.NoParentActionUndoRedoButtonGroupType
            to ActionModel.ButtonGroupActionModel(
                actionType = ActionButtonType.NoParentActionUndoRedoButtonGroupType,
                childButtonActionModels = listOf(
                    ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.UndoActionButtonType,
                        isEnabled = false,
                    ),
                    ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.RedoActionButtonType,
                        isEnabled = false,
                    ),
                ),
            ),
        ActionButtonType.TogglePreviewActionButtonType to ActionModel.ButtonActionModel(
            actionType = ActionButtonType.TogglePreviewActionButtonType,
            isSelected = true,
        ),
        ActionButtonType.NoParentActionSelectionButtonGroupType
            to ActionModel.SelectableButtonGroupActionModel(
                actionType = ActionButtonType.NoParentActionSelectionButtonGroupType,
                childButtonActionModels = listOf(
                    ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.SelectRectangleActionButtonType,
                    ),
                    ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.SelectTouchActionButtonType,
                    ),
                    ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.SelectMagicWandActionButtonType,
                    ),
                ),
            ),
        ActionButtonType.MoveSelectionActionButtonType to ActionModel.ButtonActionModel(
            actionType = ActionButtonType.MoveSelectionActionButtonType,
            isEnabled = false,
        ),
        ActionButtonType.DeselectActionButtonType to ActionModel.ButtonActionModel(
            actionType = ActionButtonType.DeselectActionButtonType,
            isEnabled = false,
        ),
        ActionButtonType.MoveImageActionButtonType to ActionModel.ButtonGroupActionModel(
            actionType = ActionButtonType.MoveImageActionButtonType,
            childButtonActionModels = listOf(
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.MoveImageUpActionButtonType,
                ),
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.MoveImageDownActionButtonType,
                ),
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.MoveImageLeftActionButtonType,
                ),
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.MoveImageRightActionButtonType,
                ),
            ),
        ),
        ActionButtonType.NoParentActionZoomButtonGroupType to ActionModel.ButtonGroupActionModel(
            actionType = ActionButtonType.NoParentActionZoomButtonGroupType,
            childButtonActionModels = listOf(
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.ZoomInActionButtonType,
                    isEnabled = true,
                ),
                ActionModel.ButtonActionModel(
                    actionType = ActionButtonType.ZoomOutActionButtonType,
                    isEnabled = true,
                ),
            ),
        ),
        ActionButtonType.SavePixelImageActionButtonType to ActionModel.ButtonActionModel(
            actionType = ActionButtonType.SavePixelImageActionButtonType,
        ),
        ActionButtonType.OpenPixelImageActionButtonType to ActionModel.ButtonActionModel(
            actionType = ActionButtonType.OpenPixelImageActionButtonType,
        ),
        ActionButtonType.ExportPixelImageActionButtonType to ActionModel.ButtonActionModel(
            actionType = ActionButtonType.ExportPixelImageActionButtonType,
        ),
    )

    private fun onPixelVisited(event: CanvasSectionEvent.PixelVisited) {
        val x = event.x
        val y = event.y

        val mainScreenState = _mainScreenState.value
        val selectedPaintTool = mainScreenState.getSelectedPaintTool()

        when {
            mainScreenState.isMoveSelectionSelected() -> updateFloatingImagePosition(x, y)
            mainScreenState.isSelectRectSelected() -> updateRectangleSelection(x, y)
            mainScreenState.isSelectTouchSelected() -> togglePixelSelected(x, y)
            mainScreenState.isSelectMagicWandSelected() -> {}
            selectedPaintTool is ActionButtonType.InkPenActionButtonType -> updatePixelColor(x, y)
            selectedPaintTool is ActionButtonType.InkBucketActionButtonType -> applyBucket(x, y)
            else -> {}
        }

        _mainScreenState.update { state ->
            state.copy(
                canvasSectionState = state.canvasSectionState.copy(
                    isVisitingPixels = true,
                    lastVisitedPixel = Pair(x, y),
                ),
            )
        }
    }

    private fun onPixelVisitCancel() {
        _mainScreenState.update { state ->
            state.copy(
                canvasSectionState = state.canvasSectionState.copy(
                    isVisitingPixels = false,
                ),
            )
        }
    }

    private fun onPixelVisitFinish() {
        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val isVisitingPixels = canvasSectionState.isVisitingPixels

        when {
            mainScreenState.isMoveSelectionSelected() -> {}
            mainScreenState.isSelectMagicWandSelected() && isVisitingPixels -> {
                canvasSectionState.lastVisitedPixel?.let { (x, y) ->
                    selectWithMagicWand(x, y)
                }
            }
            else -> {}
        }

        _mainScreenState.update { state ->
            state.copy(
                canvasSectionState = state.canvasSectionState.copy(
                    isVisitingPixels = false,
                ),
            )
        }
    }

    private fun updatePixelImage(pixelImage: PixelImageModel, isPaletteUpdate: Boolean = false) {
        _mainScreenState.update { mainScreenState ->
            val canvasSectionState = mainScreenState.canvasSectionState
            val actionsSectionState = mainScreenState.actionsSectionState
            val pixelImagePreviewSectionState =
                mainScreenState.pixelImagePreviewSectionState
            mainScreenState.copy(
                canvasSectionState = canvasSectionState.copy(
                    pixelImageModel = pixelImage,
                ),
                actionsSectionState = if (isPaletteUpdate) {
                    actionsSectionState.updatePaletteButtons(
                        palette = pixelImage.paletteModel,
                    )
                } else {
                    actionsSectionState
                },
                pixelImagePreviewSectionState = pixelImagePreviewSectionState.copy(
                    pixelImageModel = pixelImage,
                ),
            )
        }
    }

    private fun updatePixelSelection(
        pixelSelection: PixelSelectionModel?,
        floatingPixelImage: FloatingPixelImageModel?,
    ) {
        _mainScreenState.update { state ->
            state.updateSelection(
                pixelSelection,
                floatingPixelImage,
            )
        }
    }

    private fun togglePixelSelected(x: Int, y: Int) {
        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return

        viewModelScope.launch {
            togglePixelSelectionUseCase.execute(
                successBlock = { pixelSelection ->
                    updatePixelSelection(
                        pixelSelection,
                        floatingPixelImage = null,
                    )
                },
                params = TogglePixelSelectionUseCase.Params(
                    actionContext,
                    x,
                    y,
                    isSameAction = canvasSectionState.isVisitingPixels,
                ),
            )
        }
    }

    private fun updateFloatingImagePosition(x: Int, y: Int) {
        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val lastVisitedPixel = canvasSectionState.lastVisitedPixel
        val actionContext = canvasSectionState.createEditorActionContext() ?: return
        val isSameAction = canvasSectionState.isVisitingPixels

        viewModelScope.launch {
            moveFloatingImageUseCase.execute(
                successBlock = { actionContextResult ->
                    _mainScreenState.update { state ->
                        state.copy(
                            canvasSectionState = state.canvasSectionState.updateWith(
                                actionContext = actionContextResult,
                            ),
                        )
                    }
                },
                params = MoveFloatingImageUseCase.Params(
                    actionContext,
                    offsetDelta = if (!isSameAction || lastVisitedPixel == null) {
                        Pair(0, 0)
                    } else {
                        Pair(x - lastVisitedPixel.first, y - lastVisitedPixel.second)
                    },
                    isSameAction,
                ),
            )
        }
    }

    private fun updateRectangleSelection(x: Int, y: Int) {
        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return
        val isSameAction = canvasSectionState.isVisitingPixels
        val rectangleSelectionActionType = canvasSectionState.rectangleSelectionActionType

        if (!isSameAction || rectangleSelectionActionType == null) {
            val zoomFactor = canvasSectionState.zoomFactor
            startRectangleSelectionAction(x, y, actionContext, zoomFactor)
        } else {
            updateRectangleSelection(x, y, actionContext, rectangleSelectionActionType, true)
        }
    }

    private fun startRectangleSelectionAction(
        x: Int,
        y: Int,
        actionContext: PixelImageEditorActionContext,
        zoomFactor: Float,
    ) {
        viewModelScope.launch {
            getRectangleSelectionActionTypeUseCase.invoke(
                params = GetRectangleSelectionActionTypeUseCase.Params(
                    rectangleSelectionModel = actionContext.pixelSelection?.rectangleSelection,
                    x,
                    y = actionContext.pixelImage.getPixelHeight() - 1 - y,
                    zoomFactor,
                ),
            ).getOrNull()?.let { rectangleSelectionActionType ->
                _mainScreenState.update { state ->
                    state.copy(
                        canvasSectionState = state.canvasSectionState.copy(
                            rectangleSelectionActionType = rectangleSelectionActionType,
                        ),
                    )
                }
                updateRectangleSelection(
                    x,
                    y,
                    actionContext,
                    rectangleSelectionActionType,
                    isSameAction = false,
                )
            }
        }
    }

    private fun updateRectangleSelection(
        x: Int,
        y: Int,
        actionContext: PixelImageEditorActionContext,
        rectangleSelectionActionType: RectangleSelectionActionType,
        isSameAction: Boolean,
    ) {
        viewModelScope.launch {
            updateRectangleSelectionUseCase.execute(
                successBlock = { pixelSelection ->
                    updatePixelSelection(
                        pixelSelection,
                        floatingPixelImage = null,
                    )
                },
                params = UpdateRectangleSelectionUseCase.Params(
                    actionContext,
                    x,
                    y = actionContext.pixelImage.getPixelHeight() - 1 - y,
                    rectangleSelectionActionType,
                    isSameAction = isSameAction,
                ),
            )
        }
    }

    private fun selectWithMagicWand(x: Int, y: Int) {
        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return

        viewModelScope.launch {
            selectWithMagicWandUseCase.execute(
                successBlock = { pixelSelection ->
                    updatePixelSelection(
                        pixelSelection,
                        floatingPixelImage = null,
                    )
                },
                params = SelectWithMagicWandUseCase.Params(
                    actionContext,
                    x,
                    y,
                ),
            )
        }
    }

    private fun updatePixelColor(x: Int, y: Int) {
        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return
        val paletteIndex = mainScreenState.getPaletteIndex()

        viewModelScope.launch {
            updatePixelColorUseCase.execute(
                successBlock = { updatedPixelImage ->
                    updatePixelImage(updatedPixelImage)
                },
                failureBlock = { },
                params = UpdatePixelColorUseCase.Params(
                    actionContext,
                    x = x,
                    y = y,
                    paletteIndex,
                    isSameAction = canvasSectionState.isVisitingPixels,
                ),
            )
        }
    }

    private fun applyBucket(x: Int, y: Int) {
        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return
        val paletteIndex = mainScreenState.getPaletteIndex()

        viewModelScope.launch {
            applyBucketUseCase.execute(
                successBlock = { updatedPixelImage ->
                    updatePixelImage(updatedPixelImage)
                },
                failureBlock = { },
                params = ApplyBucketUseCase.Params(
                    actionContext,
                    x = x,
                    y = y,
                    paletteIndex,
                    isSameAction = canvasSectionState.isVisitingPixels,
                ),
            )
        }
    }

    private fun moveImage(direction: MoveDirection) {
        val canvasSectionState = _mainScreenState.value.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return
        val actionsSectionState = _mainScreenState.value.actionsSectionState
        val isMoveSelectionButtonSelected = actionsSectionState.isSelected(
            ActionButtonType.MoveSelectionActionButtonType,
        )

        if (isMoveSelectionButtonSelected) {
            viewModelScope.launch {
                moveFloatingImageUseCase.execute(
                    successBlock = { actionContextResult ->
                        _mainScreenState.update { state ->
                            state.copy(
                                canvasSectionState = state.canvasSectionState.updateWith(
                                    actionContext = actionContextResult,
                                ),
                            )
                        }
                    },
                    params = MoveFloatingImageUseCase.Params(
                        actionContext,
                        offsetDelta = when (direction) {
                            MoveDirection.UP -> Pair(0, 1)
                            MoveDirection.DOWN -> Pair(0, -1)
                            MoveDirection.LEFT -> Pair(-1, 0)
                            MoveDirection.RIGHT -> Pair(1, 0)
                        },
                        isSameAction = false,
                    ),
                )
            }
        } else {
            viewModelScope.launch {
                moveImageUseCase.execute(
                    successBlock = { pixelImageResult ->
                        updatePixelImage(pixelImageResult)
                    },
                    failureBlock = { },
                    params = MoveImageUseCase.Params(
                        actionContext,
                        moveDirection = direction,
                    ),
                )
            }
        }
    }

    private fun togglePreview() {
        toggleSelectableActionButton(ActionButtonType.TogglePreviewActionButtonType)
    }

    private fun toggleSelectRectangle() {
        toggleSelectButton(ActionButtonType.SelectRectangleActionButtonType)
    }

    private fun toggleSelectTouch() {
        toggleSelectButton(ActionButtonType.SelectTouchActionButtonType)
    }

    private fun toggleSelectMagicWand() {
        toggleSelectButton(ActionButtonType.SelectMagicWandActionButtonType)
    }

    private fun toggleSelectButton(actionButtonType: ActionButtonType) {
        _mainScreenState.update { state ->
            val actionsSectionState = state.actionsSectionState.updateSelectedChildButton(
                actionButtonType,
                isToggle = true,
            )
            state.copy(
                actionsSectionState = if (actionsSectionState.isSelected(actionButtonType)) {
                    actionsSectionState.disablePaintButtons()
                } else {
                    actionsSectionState.enablePaintButtons()
                },
            )
        }
    }

    private fun deselectPreviouslySelected() {
        val canvasSectionState = _mainScreenState.value.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return

        viewModelScope.launch {
            deselectUseCase.execute(
                successBlock = {
                    updatePixelSelection(
                        pixelSelection = null,
                        floatingPixelImage = null,
                    )
                },
                params = DeselectUseCase.Params(
                    actionContext,
                ),
            )
        }
    }

    private fun toggleMoveSelection() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                actionsSectionState = mainScreenState.actionsSectionState.toggleMoveSelection(),
            )
        }

        val mainScreenState = _mainScreenState.value
        val canvasSectionState = mainScreenState.canvasSectionState
        val actionContext = canvasSectionState.createEditorActionContext() ?: return

        if (mainScreenState.isMoveSelectionSelected()) {
            viewModelScope.launch {
                convertSelectionToFloatingImageUseCase.execute(
                    successBlock = { actionContextResult ->
                        _mainScreenState.update { state ->
                            state.copy(
                                canvasSectionState = state.canvasSectionState.updateWith(
                                    actionContext = actionContextResult,
                                ),
                            )
                        }
                    },
                    params = ConvertSelectionToFloatingImageUseCase.Params(
                        actionContext,
                    ),
                )
            }
        } else {
            viewModelScope.launch {
                landFloatingImageUseCase.execute(
                    successBlock = { actionContextResult ->
                        _mainScreenState.update { state ->
                            state.copy(
                                canvasSectionState = state.canvasSectionState.updateWith(
                                    actionContext = actionContextResult,
                                ),
                            )
                        }
                    },
                    params = LandFloatingImageUseCase.Params(
                        actionContext,
                    ),
                )
            }
        }
    }

    private fun updateSelectedPaletteIndex(pickPaletteColorActionButtonType: ActionButtonType) {
        _mainScreenState.update { mainScreenState ->
            val actionsSectionState = mainScreenState.actionsSectionState
            mainScreenState.copy(
                actionsSectionState = actionsSectionState.updateSelectedChildButton(
                    pickPaletteColorActionButtonType,
                ),
                isShowPalette = false,
            )
        }
    }

    private fun toggleSelectableActionButton(actionButtonType: ActionButtonType) {
        _mainScreenState.update { mainScreenState ->
            val actionsSectionState = mainScreenState.actionsSectionState
            mainScreenState.copy(
                actionsSectionState = actionsSectionState.toggleSelectableButton(
                    actionButtonType,
                ),
            )
        }
    }

    private fun updateSelectedTool(selectToolActionButtonType: ActionButtonType) {
        _mainScreenState.update { mainScreenState ->
            val actionsSectionState = mainScreenState.actionsSectionState
            mainScreenState.copy(
                actionsSectionState = actionsSectionState.updateSelectedChildButton(
                    selectToolActionButtonType,
                ),
            )
        }
    }

    private fun undoEditorAction() {
        viewModelScope.launch {
            undoEditorActionUseCase.execute(
                successBlock = { actionContext ->
                    updatePixelImage(actionContext.pixelImage)
                    updatePixelSelection(
                        pixelSelection = actionContext.pixelSelection,
                        floatingPixelImage = actionContext.floatingPixelImage,
                    )
                },
                params = UseCaseParams.NoParams,
            )
        }
    }

    private fun redoEditorAction() {
        viewModelScope.launch {
            redoEditorActionUseCase.execute(
                successBlock = { actionContext ->
                    updatePixelImage(actionContext.pixelImage)
                    updatePixelSelection(
                        pixelSelection = actionContext.pixelSelection,
                        floatingPixelImage = actionContext.floatingPixelImage,
                    )
                },
                params = UseCaseParams.NoParams,
            )
        }
    }

    private fun zoom(isZoomIn: Boolean) {
        viewModelScope.launch {
            getNextZoomFactorUseCase.execute(
                successBlock = { zoomFactor ->
                    _mainScreenState.update { mainScreenState ->
                        val canvasSectionState = mainScreenState.canvasSectionState
                        val actionsSectionState = mainScreenState.actionsSectionState
                        mainScreenState.copy(
                            canvasSectionState = canvasSectionState.copy(
                                zoomFactor = zoomFactor,
                            ),
                            actionsSectionState = actionsSectionState.updateZoomButtonState(
                                zoomFactor,
                            ),
                        )
                    }
                },
                failureBlock = { },
                params = GetNextZoomFactorUseCase.Params(
                    currentZoomFactor = _mainScreenState.value.canvasSectionState.zoomFactor,
                    isZoomIn = isZoomIn,
                    maxZoomFactor = MAX_ZOOM_FACTOR,
                    minZoomFactor = MIN_ZOOM_FACTOR,
                ),
            )
        }
    }

    private fun selectSavePixelImageLocation() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectSavePixelImage = true,
                ),
            )
        }
    }

    fun onSelectSavePixelImageLocationLaunched() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectSavePixelImage = false,
                ),
            )
        }
    }

    private fun selectExportPixelImageFormat() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                isShowSelectPixelImageExportFormat = true,
            )
        }
    }

    fun hideExportPixelImageFormatSelection() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                isShowSelectPixelImageExportFormat = false,
            )
        }
    }

    fun onPixelImageExportFormatSelected(exportFormat: PixelImageExportFormat) {
        selectExportPixelImageLocation(exportFormat)
    }

    private fun selectExportPixelImageLocation(exportFormat: PixelImageExportFormat) {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                isShowSelectPixelImageExportFormat = false,
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectExportPixelImage = true,
                    selectedPixelImageExportFormat = exportFormat,
                ),
            )
        }
    }

    fun onSelectExportPixelImageLocationLaunched() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectExportPixelImage = false,
                ),
            )
        }
    }

    private fun selectOpenPixelImageLocation() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = true,
                ),
            )
        }
    }

    fun onSelectOpenPixelImageLocationLaunched() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = false,
                ),
            )
        }
    }

    fun onSelectSavePixelImageLocationResult(result: Result<Uri>) {
        result.fold(
            onSuccess = { uri ->
                val pixelImageModel =
                    _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
                savePixelImage(pixelImageModel, uri)
            },
            onFailure = {
                // TODO
            },
        )
    }

    private fun savePixelImage(pixelImageModel: PixelImageModel, uri: Uri) {
        viewModelScope.launch {
            savePixelImageUseCase.execute(
                successBlock = { },
                failureBlock = { }, // TODO
                params = SavePixelImageUseCase.Params(
                    pixelImageModel,
                    uri,
                ),
            )
        }
    }

    fun onSelectExportPixelImageLocationResult(result: Result<Uri>) {
        result.fold(
            onSuccess = { uri ->
                val pixelImageModel =
                    _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
                exportPixelImage(
                    pixelImageModel,
                    uri,
                    _mainScreenState.value.launcherState.selectedPixelImageExportFormat,
                )
            },
            onFailure = {
                // TODO
            },
        )
        _mainScreenState.update { state ->
            state.copy(
                launcherState = state.launcherState.copy(
                    selectedPixelImageExportFormat = null,
                ),
            )
        }
    }

    private fun exportPixelImage(
        pixelImageModel: PixelImageModel,
        uri: Uri,
        exportFormat: PixelImageExportFormat?,
    ) {
        val onSuccess: () -> Unit = {
            _mainScreenState.update { state ->
                state.copy(
                    isShowPixelImageExportSuccess = true,
                )
            }
        }
        val onFailure: (ExportPixelImageError?) -> Unit = { error ->
            _mainScreenState.update { state ->
                state.copy(
                    exportPixelImageError = error,
                )
            }
        }

        when (exportFormat) {
            PixelImageExportFormat.PNG -> exportPixelImageToPng(
                pixelImageModel,
                uri,
                onSuccess,
                onFailure,
            )
            PixelImageExportFormat.SVG, null -> exportPixelImageToSvg(
                pixelImageModel,
                uri,
                onSuccess,
                onFailure,
            )
        }
    }

    private fun exportPixelImageToPng(
        pixelImageModel: PixelImageModel,
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (ExportPixelImageError?) -> Unit,
    ) {
        viewModelScope.launch {
            exportPixelImageToPngUseCase.execute(
                successBlock = { onSuccess() },
                failureBlock = { error -> onFailure(error as? ExportPixelImageError) },
                params = ExportPixelImageToPngUseCase.Params(
                    pixelImageModel,
                    uri,
                ),
            )
        }
    }

    private fun exportPixelImageToSvg(
        pixelImageModel: PixelImageModel,
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (ExportPixelImageError?) -> Unit,
    ) {
        viewModelScope.launch {
            exportPixelImageToSvgUseCase.execute(
                successBlock = { onSuccess() },
                failureBlock = { error -> onFailure(error as? ExportPixelImageError) },
                params = ExportPixelImageToSvgUseCase.Params(
                    pixelImageModel,
                    uri,
                ),
            )
        }
    }

    fun onSelectOpenPixelImageLocationResult(result: Result<Uri>) {
        result.fold(
            onSuccess = { uri ->
                openPixelImage(uri)
            },
            onFailure = {
                // TODO
            },
        )
    }

    private fun openPixelImage(uri: Uri) {
        viewModelScope.launch {
            openPixelImageUseCase.execute(
                successBlock = { pixelImage ->
                    _mainScreenState.update { state ->
                        state.copy(
                            canvasSectionState = state.canvasSectionState.copy(
                                pixelImageModel = pixelImage,
                                zoomFactor = DEFAULT_ZOOM_FACTOR,
                            ),
                            actionsSectionState = state.actionsSectionState.updatePaletteButtons(
                                palette = pixelImage.paletteModel,
                            ),
                            pixelImagePreviewSectionState = state.pixelImagePreviewSectionState
                                .copy(
                                    pixelImageModel = pixelImage,
                                ),
                        )
                    }
                },
                failureBlock = { error ->
                    _mainScreenState.update { state ->
                        state.copy(
                            openPixelImageError = error as? OpenPixelImageError,
                        )
                    }
                },
                params = OpenPixelImageUseCase.Params(
                    uri,
                ),
            )
        }
    }
}
