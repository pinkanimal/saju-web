package com.pinkanimal.weekendcourse.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.pinkanimal.weekendcourse.R
import com.pinkanimal.weekendcourse.data.local.PlaceEntity

object NotificationHelper {

    private const val CHANNEL_ID = "weekend_course_channel"
    private const val CHANNEL_NAME = "주말 코스 알림"
    private const val CHANNEL_DESC = "매주 금요일 저녁 주말 코스를 추천해드립니다"

    fun createChannel(context: Context) = createNotificationChannel(context)

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESC
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun sendCourseNotification(context: Context, course: WeekendCourse, placeCount: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val satSummary = course.saturday?.let { "${it.region} ${it.places.size}곳" } ?: ""
        val sunSummary = course.sunday?.let { "${it.region} ${it.places.size}곳" } ?: ""
        val body = buildString {
            append("이번 주말 코스: ")
            if (satSummary.isNotEmpty()) append("토 $satSummary ")
            if (sunSummary.isNotEmpty()) append("일 $sunSummary ")
            append("| ${course.estimatedCostRange}")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("주말 코스가 준비됐어요 ($placeCount곳)")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }

    fun sendReminderNotification(context: Context, place: PlaceEntity) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val body = "저장된 장소가 아직 적어요. \"${place.name}\" — ${place.oneLineNote}"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("주말에 가볼 곳을 더 모아볼까요?")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(1002, notification)
    }
}
