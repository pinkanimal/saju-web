package com.pinkanimal.weekendcourse.data.repository

import com.pinkanimal.weekendcourse.data.remote.ClaudeApi
import com.pinkanimal.weekendcourse.data.remote.ClaudeRequest
import com.pinkanimal.weekendcourse.data.remote.ExtractedPlace
import com.pinkanimal.weekendcourse.data.remote.Message
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LlmRepositoryImpl @Inject constructor(
    private val claudeApi: ClaudeApi
) : LlmRepository {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val systemPrompt = """
        아래 OCR 텍스트는 SNS 스크린샷에서 추출됐고 해시태그·UI·댓글이 섞여 있다.
        장소 정보만 뽑아 JSON만 출력하라(설명·마크다운 금지).
        스키마: {found, name, address, category, signatureMenu, priceHint, oneLineNote, confidence}
    """.trimIndent()

    override suspend fun extract(ocrText: String): Result<ExtractedPlace> {
        return try {
            val request = ClaudeRequest(
                system = systemPrompt,
                messages = listOf(Message(role = "user", content = ocrText))
            )
            val response = claudeApi.sendMessage(request)
            val rawText = response.content.firstOrNull()?.text ?: return Result.failure(Exception("Empty response"))
            val cleaned = rawText
                .replace(Regex("^```json\\s*"), "")
                .replace(Regex("```\\s*$"), "")
                .trim()
            val place = json.decodeFromString<ExtractedPlace>(cleaned)
            Result.success(place)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
