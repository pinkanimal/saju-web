package com.pinkanimal.weekendcourse.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class OcrEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recognizer = TextRecognition.getClient(
        KoreanTextRecognizerOptions.Builder().build()
    )

    suspend fun recognizeText(uri: Uri): Result<String> = suspendCancellableCoroutine { cont ->
        try {
            val image = InputImage.fromFilePath(context, uri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.textBlocks.joinToString("\n") { it.text }
                    cont.resume(Result.success(text))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            cont.resume(Result.failure(e))
        }
    }
}
