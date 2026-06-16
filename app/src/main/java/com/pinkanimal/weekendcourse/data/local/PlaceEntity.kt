package com.pinkanimal.weekendcourse.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String?,
    val category: String,
    val signatureMenu: String?,
    val priceHint: String?,
    val oneLineNote: String,
    val source: String = "unknown",
    val kakaoPlaceId: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val region2depth: String? = null,
    val openingHours: String? = null,
    val thumbnailPath: String,
    val status: String,
    val savedAt: Long = System.currentTimeMillis()
)
