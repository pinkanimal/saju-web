package com.pinkanimal.weekendcourse.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class KakaoSearchResponse(val documents: List<KakaoPlace>)

@Serializable
data class KakaoPlace(
    val id: String = "",
    val place_name: String = "",
    val address_name: String = "",
    val road_address_name: String = "",
    val x: String = "",  // longitude
    val y: String = "",  // latitude
    val category_group_name: String = "",
    val phone: String = ""
)
