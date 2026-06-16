package com.pinkanimal.weekendcourse.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClaudeRequest(
    val model: String = "claude-haiku-4-5",
    @SerialName("max_tokens") val max_tokens: Int = 512,
    val system: String,
    val messages: List<Message>
)

@Serializable
data class Message(val role: String, val content: String)

@Serializable
data class ClaudeResponse(val content: List<ContentBlock>)

@Serializable
data class ContentBlock(val type: String, val text: String = "")

@Serializable
data class ExtractedPlace(
    val found: Boolean,
    val name: String = "",
    val address: String = "",
    val category: String = "기타",
    val signatureMenu: String = "",
    val priceHint: String = "",
    val oneLineNote: String = "",
    val confidence: String = "low"
)
