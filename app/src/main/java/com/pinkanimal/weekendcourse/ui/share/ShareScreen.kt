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
                        onClick = { /* Phase 2 placeholder */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("저장하기")
                    }
                }
            }
            is ShareUiState.Error -> {
                Text(
                    text = "오류: ${state.msg}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
