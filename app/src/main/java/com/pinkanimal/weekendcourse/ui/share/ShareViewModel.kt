package com.pinkanimal.weekendcourse.ui.share

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    data class Error(val msg: String) : ShareUiState()
}

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val ocrEngine: OcrEngine
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
                        _uiState.value = ShareUiState.OcrResult(text, internalUri)
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
