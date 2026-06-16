# WeekendCourse — 주말 데이트 코스 앱

SNS 스크린샷을 공유하면 장소를 자동 정리하고 매주 금요일 저녁에 주말 데이트 코스를 알림으로 보내주는 Android 앱.

## 기술 스택
- **언어**: Kotlin 2.0.21
- **UI**: Jetpack Compose (Material 3), Compose BOM 2024.12.01
- **아키텍처**: MVVM + Repository, 단일 모듈
- **DI**: Hilt 2.52 (KSP)
- **DB**: Room 2.7.0
- **비동기**: Coroutines + Flow
- **네트워킹**: Retrofit 2.11.0 + OkHttp 4.12.0 + kotlinx.serialization 1.7.3
- **OCR**: ML Kit Text Recognition Korean 16.0.1 (온디바이스)
- **이미지**: Coil 2.7.0
- **백그라운드**: WorkManager 2.10.0
- **minSdk**: 26 / **targetSdk**: 35

## 패키지 구조
```
com.pinkanimal.weekendcourse
├─ di/                    Hilt 모듈
├─ data/
│  ├─ local/              Room: Entity, Dao, Database
│  ├─ remote/             Retrofit: ClaudeApi, KakaoApi, DTO
│  └─ repository/         PlaceRepository, LlmRepository, MapRepository, CourseRepository
├─ domain/                모델, 유즈케이스
├─ ocr/                   ML Kit 래퍼 + 휴리스틱 게이트
├─ work/                  WorkManager: FridayDigestWorker
├─ ui/
│  ├─ share/              공유 수신 → 확인 화면
│  ├─ list/               저장한 장소 목록
│  ├─ course/             주말 코스 화면
│  └─ theme/
└─ MainActivity.kt
```

## 빌드 방법

1. `local.properties` 파일 생성 (절대 커밋 금지):
   ```
   sdk.dir=/path/to/android/sdk
   CLAUDE_API_KEY=sk-ant-...
   KAKAO_REST_API_KEY=...
   ```
2. `./gradlew assembleDebug`

## Phase 계획
- **Phase 0** ✅ — 프로젝트 스캐폴드, Hilt/Compose/Room/Retrofit 의존성
- **Phase 1** — 공유 인텐트 수신 → 이미지 복사 → ML Kit OCR → raw 텍스트 표시
- **Phase 2** — 휴리스틱 게이트 + Claude 추출 + 확인 화면 + Room 저장 + 목록 화면
- **Phase 3** — Kakao Local 정규화 (좌표·구·영업시간)
- **Phase 4** — WorkManager 금요일 19:00 + 코스 생성 + 알림
- **Phase 5** — 카카오맵 딥링크 + UI 다듬기

## 외부 API
- **Claude API**: `https://api.anthropic.com/v1/messages`, 모델 `claude-haiku-4-5`
- **Kakao Local**: 키워드 장소 검색, `Authorization: KakaoAK {KEY}` 헤더
