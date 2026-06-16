package com.pinkanimal.weekendcourse.work

import com.pinkanimal.weekendcourse.data.local.PlaceEntity

data class DayCourse(val day: String, val places: List<PlaceEntity>, val region: String)
data class WeekendCourse(
    val saturday: DayCourse?,
    val sunday: DayCourse?,
    val estimatedCostRange: String
)

object CourseScheduler {

    fun buildCourse(places: List<PlaceEntity>): WeekendCourse {
        if (places.isEmpty()) return WeekendCourse(null, null, "")

        // Group by region2depth (구), fallback to "기타"
        val grouped = places.groupBy { it.region2depth ?: "기타" }
            .entries.sortedByDescending { it.value.size }

        val satGroup = grouped.getOrNull(0)
        val sunGroup = grouped.getOrNull(1) ?: satGroup

        val satPlaces = satGroup?.value?.let(::sortByProximity) ?: emptyList()
        val sunPlaces = if (grouped.size > 1) sunGroup?.value?.let(::sortByProximity) ?: emptyList() else emptyList()

        val allPlaces = (satPlaces + sunPlaces).distinct()
        val cost = CostConstants.estimateForCouple(allPlaces)

        return WeekendCourse(
            saturday = satGroup?.let { DayCourse("토요일", satPlaces, it.key) },
            sunday = if (grouped.size > 1) sunGroup?.let { DayCourse("일요일", sunPlaces, it.key) } else null,
            estimatedCostRange = cost
        )
    }

    // Simple nearest-neighbor: sort by lat then lng
    private fun sortByProximity(places: List<PlaceEntity>): List<PlaceEntity> {
        val withCoords = places.filter { it.lat != null && it.lng != null }
        val withoutCoords = places.filter { it.lat == null || it.lng == null }
        // Prioritize restaurants for lunch/dinner slots
        val restaurants = withCoords.filter { it.category == "식당" }
        val others = withCoords.filter { it.category != "식당" }
        return (others + restaurants + withoutCoords)
    }
}
