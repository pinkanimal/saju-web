package com.pinkanimal.weekendcourse.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.pinkanimal.weekendcourse.data.local.PlaceEntity

object KakaoMapHelper {
    fun openNavigation(context: Context, place: PlaceEntity) {
        // Try kakaoPlaceId first, then lat/lng, then name search
        // If KakaoMap not installed: browser fallback
        val kakaoIntent = when {
            place.kakaoPlaceId != null -> Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://place?id=${place.kakaoPlaceId}"))
            place.lat != null && place.lng != null -> Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://route?ep=${place.lat},${place.lng}&by=FOOT"))
            else -> Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://search?q=${Uri.encode(place.name)}"))
        }
        try {
            context.startActivity(kakaoIntent)
        } catch (e: ActivityNotFoundException) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://map.kakao.com/link/search/${Uri.encode(place.name)}"))
            context.startActivity(browserIntent)
        }
    }

    fun openPlacePage(context: Context, place: PlaceEntity) {
        val kakaoIntent = if (place.kakaoPlaceId != null) {
            Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://place?id=${place.kakaoPlaceId}"))
        } else {
            Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://search?q=${Uri.encode(place.name)}"))
        }
        try {
            context.startActivity(kakaoIntent)
        } catch (e: ActivityNotFoundException) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://map.kakao.com/link/search/${Uri.encode(place.name)}"))
            context.startActivity(browserIntent)
        }
    }
}
