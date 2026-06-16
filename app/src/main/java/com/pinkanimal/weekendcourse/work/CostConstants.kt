package com.pinkanimal.weekendcourse.work

import com.pinkanimal.weekendcourse.data.local.PlaceEntity

object CostConstants {
    val COST_PER_PERSON_KRW = mapOf(
        "카페" to 15_000,
        "식당" to 30_000,
        "전시" to 20_000,
        "팝업" to 15_000,
        "바" to 25_000,
        "기타" to 15_000
    )

    fun estimateForCouple(places: List<PlaceEntity>): String {
        val total = places.sumOf { COST_PER_PERSON_KRW[it.category] ?: 15_000 } * 2
        val low = total * 8 / 10 / 10_000
        val high = total * 12 / 10 / 10_000
        return "약 ${low}~${high}만원"
    }
}
