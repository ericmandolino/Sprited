package com.swirlfist.sprited.presentation.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.sprited.domain.error.OpenPixelImageError
import com.swirlfist.sprited.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.sprited.domain.usecase.UpdateBasePixelImageUseCase
import com.swirlfist.sprited.domain.usecase.execute
import com.swirlfist.sprited.presentation.state.StartScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val updateBasePixelImageUseCase: UpdateBasePixelImageUseCase,
    private val openPixelImageUseCase: OpenPixelImageUseCase,
) : ViewModel() {
    private val _startScreenState = MutableStateFlow(
        value = StartScreenState()
    )
    val startScreenState = _startScreenState.asStateFlow()

    fun clearOpenPixelImageError() {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                openPixelImageError = null,
            )
        }
    }

    fun openImageSelection() {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                launcherState = startScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = true,
                ),
            )
        }
    }

    fun onSelectOpenPixelImageLocationLaunched() {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                launcherState = startScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = false,
                ),
            )
        }
    }

    fun onSelectOpenPixelImageLocationResult(
        result: Result<Uri>,
    ) {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                isLoadingImage = true,
            )
        }

        result.fold(
            onSuccess = { uri ->
                openPixelImage(uri)
            },
            onFailure = {
                _startScreenState.update { startScreenState ->
                    startScreenState.copy(
                        isLoadingImage = false,
                    )
                }
            }
        )
    }

    private fun openPixelImage(uri: Uri) {
        viewModelScope.launch {
            openPixelImageUseCase.execute(
                successBlock = { loadedPixelImage ->
                    viewModelScope.launch {
                        updateBasePixelImageUseCase(
                            UpdateBasePixelImageUseCase.Params(loadedPixelImage)
                        )
                        _startScreenState.update { startScreenState ->
                            startScreenState.copy(
                                isNavigateToMainExpected = true,
                            )
                        }
                    }
                },
                failureBlock = { error ->
                    _startScreenState.update { startScreenState ->
                        startScreenState.copy(
                            isLoadingImage = false,
                            openPixelImageError = error as? OpenPixelImageError,
                        )
                    }
                },
                params = OpenPixelImageUseCase.Params(
                    uri
                ),
            )
        }
    }

    fun onNavigateToMainAfterImageLoaded() {
        _startScreenState.update { startScreenState ->
            startScreenState.copy(
                isLoadingImage = false,
                isNavigateToMainExpected = false,
            )
        }
    }
}