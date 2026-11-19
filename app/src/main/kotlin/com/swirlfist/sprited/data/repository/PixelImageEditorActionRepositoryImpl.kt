package com.swirlfist.sprited.data.repository

import com.swirlfist.sprited.domain.model.FloatingPixelImageModel
import com.swirlfist.sprited.domain.model.PixelImageEditorAction
import com.swirlfist.sprited.domain.model.PixelImageEditorActionContext
import com.swirlfist.sprited.domain.model.PixelImageModel
import com.swirlfist.sprited.domain.model.PixelSelectionModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PixelImageEditorActionRepositoryImpl @Inject constructor() :
    PixelImageEditorActionRepository {
    private var actions = mutableListOf<PixelImageEditorAction>()
    private var currentActionIndex: Int = -1
    private var undoAvailableFlow = MutableStateFlow(false)
    private var redoAvailableFlow = MutableStateFlow(false)
    private var finalPixelImage: PixelImageModel? = null
    private var finalPixelSelection: PixelSelectionModel? = null
    private var finalFloatingPixelImage: FloatingPixelImageModel? = null

    override suspend fun getCurrentAction(): PixelImageEditorAction? =
        actions.getOrNull(currentActionIndex)

    override suspend fun getActions(): List<PixelImageEditorAction> = actions.toList()

    override suspend fun clearActions() {
        actions.clear()
        currentActionIndex = -1
        updateAvailableOperations()
    }

    override suspend fun addAction(
        action: PixelImageEditorAction,
        actionContextResult: PixelImageEditorActionContext,
    ) {
        clearActionsFromIndex(currentActionIndex + 1)
        if (actions.size == MAX_UNDO_ACTIONS) {
            actions = actions.subList(1, MAX_UNDO_ACTIONS)
        }
        actions.add(action)
        currentActionIndex = actions.size - 1
        finalPixelImage = actionContextResult.pixelImage
        finalPixelSelection = actionContextResult.pixelSelection
        finalFloatingPixelImage = actionContextResult.floatingPixelImage
        updateAvailableOperations()
    }

    override suspend fun updateLastAction(actionContextResult: PixelImageEditorActionContext) {
        if (currentActionIndex != actions.size - 1) {
            return
        }

        finalPixelImage = actionContextResult.pixelImage
        finalPixelSelection = actionContextResult.pixelSelection
        finalFloatingPixelImage = actionContextResult.floatingPixelImage
    }

    override suspend fun undoAction(): PixelImageEditorActionContext? {
        if (currentActionIndex !in actions.indices) {
            return null
        }

        val currentAction = actions[currentActionIndex]
        currentActionIndex--
        updateAvailableOperations()

        return currentAction.actionContext
    }

    override suspend fun redoAction(): PixelImageEditorActionContext? {
        val newCurrentActionIndex = currentActionIndex + 1
        return if (newCurrentActionIndex !in actions.indices) {
            null
        } else {
            currentActionIndex = newCurrentActionIndex
            updateAvailableOperations()
            val nextActionIndex = currentActionIndex + 1
            if (nextActionIndex in actions.indices) {
                val nextAction = actions[nextActionIndex]
                nextAction.actionContext
            } else {
                finalPixelImage?.let { pixelImage ->
                    PixelImageEditorActionContext(
                        pixelImage,
                        finalPixelSelection,
                        finalFloatingPixelImage,
                    )
                }
            }
        }
    }

    override fun isUndoAvailable(): Flow<Boolean> = undoAvailableFlow.asStateFlow()

    override fun isRedoAvailable(): Flow<Boolean> = redoAvailableFlow.asStateFlow()

    private fun clearActionsFromIndex(actionIndex: Int) {
        if (actionIndex !in actions.indices) {
            return
        }

        actions = actions.subList(0, actionIndex)
        currentActionIndex = actions.size - 1
    }

    private fun updateAvailableOperations() {
        undoAvailableFlow.update {
            currentActionIndex in actions.indices
        }
        redoAvailableFlow.update {
            (currentActionIndex + 1) in actions.indices
        }
    }
}
