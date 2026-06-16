package com.pinkanimal.weekendcourse.data.repository

import com.pinkanimal.weekendcourse.data.local.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun savePlace(entity: PlaceEntity): Long
    fun getAllPlaces(): Flow<List<PlaceEntity>>
    suspend fun deletePlace(entity: PlaceEntity)
    suspend fun confirmPlace(id: Long)
}
