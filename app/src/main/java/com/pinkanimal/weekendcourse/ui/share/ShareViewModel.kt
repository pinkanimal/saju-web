package com.pinkanimal.weekendcourse.ui.share

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinkanimal.weekendcourse.data.local.PlaceEntity
import com.pinkanimal.weekendcourse.data.remote.ExtractedPlace
import com.pinkanimal.weekendcourse.data.repository.LlmRepository
import com.pinkanimal.weekendcourse.data.repository.PlaceRepository
import com.pinkanimal.weekendcourse.ocr.HeuristicGate
import com.pinkanimal.weekendcourse.ocr.OcrEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

sealed class ShareUiState {
    object Idle : ShareUiState()
    object Processing : ShareUiState()
    data class OcrResult(val text: String, val imageUri: Uri) : ShareUiState()
    data class ConfirmationNeeded(val place: ExtractedPlace, val imageUri: Uri) : ShareUiState()
    data class Saved(val name: String) : ShareUiState()
    data class NotAPlace(val text: String) : ShareUiState()
    data class Error(val msg: String) : ShareUiState()
}

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val ocrEngine: OcrEngine,
    private val heuristicGate: HeuristicGate,
    private val llmRepository: LlmRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Idle)
    val uiState: StateFlow<ShareUiState> = _uiState

    fun processSharedImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.value = ShareUiState.Processing
            try {
                val internalUri = withContext(Dispatchers.IO) {
                    copyImageToInternalStorage(uri, context)
                }
                val result = ocrEngine.recognizeText(internalUri)
                result.fold(
                    onSuccess = { text ->
                        if (!heuristicGate.passes(text)) {
                            _uiState.value = ShareUiState.NotAPlace(text)
                            return@fold
                        }
                        _uiState.value = ShareUiState.Processing
                        val extractResult = llmRepository.extract(text)
                        extractResult.fold(
                            onSuccess = { place ->
                                if (place.found) {
                                    _uiState.value = ShareUiState.ConfirmationNeeded(place, internalUri)
                                } else {
                                    _uiState.value = ShareUiState.NotAPlace(text)
                                }
                            },
                            onFailure = { e ->
                                // Fallback to raw OCR if LLM fails
                                _uiState.value = ShareUiState.OcrResult(text, internalUri)
                            }
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = ShareUiState.Error(e.message ?: "OCR 실패")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = ShareUiState.Error(e.message ?: "처리 실패")
            }
        }
    }

    fun confirmSave(place: ExtractedPlace, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val entity = PlaceEntity(
                    name = place.name,
                    address = place.address.ifBlank { null },
                    category = place.category,
                    signatureMenu = place.signatureMenu.ifBlank { null },
                    priceHint = place.priceHint.ifBlank { null },
                    oneLineNote = place.oneLineNote,
                    source = "share",
                    thumbnailPath = imageUri.toString(),
                    status = "SAVED"
                )
                placeRepository.savePlace(entity)
                _uiState.value = ShareUiState.Saved(place.name)
            } catch (e: Exception) {
                _uiState.value = ShareUiState.Error(e.message ?: "저장 실패")
            }
        }
    }

    fun discardAndReset() {
        _uiState.value = ShareUiState.Idle
    }

    private fun copyImageToInternalStorage(uri: Uri, context: Context): Uri {
        val screenshotsDir = File(context.filesDir, "screenshots").apply { mkdirs() }
        val destFile = File(screenshotsDir, "${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return Uri.fromFile(destFile)
    }

    fun reset() {
        _uiState.value = ShareUiState.Idle
    }
}
