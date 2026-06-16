package com.pinkanimal.weekendcourse.ui.share

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun ShareScreen(
    imageUri: Uri,
    viewModel: ShareViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(imageUri) {
        if (uiState is ShareUiState.Idle) {
            viewModel.processSharedImage(imageUri, context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is ShareUiState.Idle -> {
                // Will trigger LaunchedEffect
            }
            is ShareUiState.Processing -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("스크린샷 분석 중...")
                }
            }
            is ShareUiState.OcrResult -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = "공유된 이미지",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Text(
                        text = state.text,
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    )
                    Button(
                        onClick = { /* Phase 3 placeholder */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("저장하기")
                    }
                }
            }
            is ShareUiState.ConfirmationNeeded -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = state.imageUri,
                        contentDescription = "공유된 이미지",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Text(
                        text = "이 장소를 저장할까요?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlaceInfoRow(label = "이름", value = state.place.name)
                            if (state.place.address.isNotBlank()) {
                                PlaceInfoRow(label = "주소", value = state.place.address)
                            }
                            PlaceInfoRow(label = "카테고리", value = state.place.category)
                            if (state.place.signatureMenu.isNotBlank()) {
                                PlaceInfoRow(label = "대표 메뉴", value = state.place.signatureMenu)
                            }
                            if (state.place.priceHint.isNotBlank()) {
                                PlaceInfoRow(label = "가격대", value = state.place.priceHint)
                            }
                            if (state.place.oneLineNote.isNotBlank()) {
                                PlaceInfoRow(label = "한줄평", value = state.place.oneLineNote)
                            }
                            PlaceInfoRow(label = "신뢰도", value = state.place.confidence)
                            state.kakaoPlace?.road_address_name?.takeIf { it.isNotBlank() }?.let {
                                PlaceInfoRow(label = "카카오 주소", value = "📍 $it")
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.discardAndReset() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("취소")
                        }
                        Button(
                            onClick = { viewModel.confirmSave(state.place, state.imageUri, state.kakaoPlace) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("저장")
                        }
                    }
                }
            }
            is ShareUiState.Saved -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "저장 완료!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "\"${state.name}\" 이(가) 장소 목록에 추가되었습니다.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = { viewModel.reset() }) {
                        Text("확인")
                    }
                }
            }
            is ShareUiState.NotAPlace -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "장소 정보를 찾을 수 없어요",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "장소가 포함된 스크린샷을 공유해주세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = { viewModel.reset() }) {
                        Text("닫기")
                    }
                }
            }
            is ShareUiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "오류: ${state.msg}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.reset() }) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
