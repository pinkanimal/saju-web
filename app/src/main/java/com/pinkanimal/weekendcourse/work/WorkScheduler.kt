package com.pinkanimal.weekendcourse.work

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun scheduleFridayDigest(context: Context) {
        val delay = millisUntilNextFriday19()
        val request = PeriodicWorkRequestBuilder<FridayDigestWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "friday_digest",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun triggerNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<FridayDigestWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }

    private fun millisUntilNextFriday19(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.WEEK_OF_YEAR, 1)
        }
        return (target.timeInMillis - now.timeInMillis).coerceAtLeast(0)
    }
}
