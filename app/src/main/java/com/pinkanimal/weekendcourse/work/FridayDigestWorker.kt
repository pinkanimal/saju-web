package com.pinkanimal.weekendcourse.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pinkanimal.weekendcourse.data.local.PlaceDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FridayDigestWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val placeDao: PlaceDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val places = placeDao.getSavedPlacesSince(sevenDaysAgo)

        NotificationHelper.createChannel(applicationContext)

        return when {
            places.isEmpty() -> Result.success()
            places.size < 3 -> {
                NotificationHelper.sendReminderNotification(applicationContext, places.first())
                Result.success()
            }
            else -> {
                val course = CourseScheduler.buildCourse(places)
                NotificationHelper.sendCourseNotification(applicationContext, course, places.size)
                Result.success()
            }
        }
    }
}
