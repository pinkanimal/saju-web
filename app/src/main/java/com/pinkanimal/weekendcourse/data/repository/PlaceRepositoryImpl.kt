package com.pinkanimal.weekendcourse.data.repository

import com.pinkanimal.weekendcourse.data.local.PlaceDao
import com.pinkanimal.weekendcourse.data.local.PlaceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao
) : PlaceRepository {

    override suspend fun savePlace(entity: PlaceEntity): Long = placeDao.insert(entity)

    override fun getAllPlaces(): Flow<List<PlaceEntity>> = placeDao.getAllPlaces()

    override suspend fun deletePlace(entity: PlaceEntity) = placeDao.delete(entity)

    override suspend fun confirmPlace(id: Long) = placeDao.confirmPlace(id)
}
