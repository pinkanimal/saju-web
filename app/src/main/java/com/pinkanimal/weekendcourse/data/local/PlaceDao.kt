package com.pinkanimal.weekendcourse.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places ORDER BY savedAt DESC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    @Insert
    suspend fun insert(place: PlaceEntity): Long

    @Delete
    suspend fun delete(place: PlaceEntity)

    @Query("SELECT * FROM places WHERE status = 'SAVED' AND lat IS NOT NULL AND savedAt >= :since")
    suspend fun getSavedPlacesSince(since: Long): List<PlaceEntity>

    @Query("UPDATE places SET status = 'SAVED' WHERE id = :id")
    suspend fun confirmPlace(id: Long)
}
