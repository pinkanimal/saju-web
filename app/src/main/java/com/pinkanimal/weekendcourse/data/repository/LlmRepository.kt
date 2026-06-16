package com.pinkanimal.weekendcourse.data.repository

import com.pinkanimal.weekendcourse.data.remote.ExtractedPlace

interface LlmRepository {
    suspend fun extract(ocrText: String): Result<ExtractedPlace>
}
