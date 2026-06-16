package com.pinkanimal.weekendcourse.ocr

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeuristicGate @Inject constructor() {

    private val ADDRESS_PATTERNS = listOf(
        Regex("(서울|부산|인천|대구|광주|대전|울산|세종|경기|강원|충북|충남|전북|전남|경북|경남|제주).{1,10}(구|시|군).{1,20}(로|길|동)\\s*\\d"),
        Regex("(강남|홍대|이태원|신촌|압구정|청담|성수|망원|한남|연남|익선|을지로|종로|명동|신사|가로수길).{1,30}(로|길|동)"),
        Regex("\\d{1,5}(번지|번길|호)?\\s*(\\d층)?")
    )

    private val PLACE_KEYWORDS = listOf(
        "카페", "맛집", "식당", "영업", "메뉴", "예약", "전시", "팝업",
        "레스토랑", "브런치", "디저트", "베이커리", "바 ", "펍", "이자카야", "오마카세",
        "오픈", "웨이팅", "웨이팅있", "예약필수", "내돈내산", "방문", "후기", "리뷰"
    )

    fun passes(text: String): Boolean {
        if (ADDRESS_PATTERNS.any { it.containsMatchIn(text) }) return true
        if (PLACE_KEYWORDS.any { text.contains(it) }) return true
        return false
    }
}
