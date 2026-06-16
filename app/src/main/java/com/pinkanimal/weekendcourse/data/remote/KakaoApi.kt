package com.pinkanimal.weekendcourse.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoApi {
    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Query("query") query: String,
        @Query("size") size: Int = 1
    ): KakaoSearchResponse
}
