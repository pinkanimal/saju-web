package com.pinkanimal.weekendcourse.data.repository

import com.pinkanimal.weekendcourse.data.remote.KakaoApi
import com.pinkanimal.weekendcourse.data.remote.KakaoPlace
import javax.inject.Inject

interface MapRepository {
    suspend fun searchPlace(query: String, address: String? = null): Result<KakaoPlace?>
}

class MapRepositoryImpl @Inject constructor(
    private val kakaoApi: KakaoApi
) : MapRepository {
    override suspend fun searchPlace(query: String, address: String?): Result<KakaoPlace?> {
        return try {
            val searchQuery = if (!address.isNullOrBlank()) "$query $address" else query
            val response = kakaoApi.searchKeyword(searchQuery)
            Result.success(response.documents.firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
