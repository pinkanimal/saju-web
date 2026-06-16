package com.pinkanimal.weekendcourse.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pinkanimal.weekendcourse.data.local.PlaceEntity
import com.pinkanimal.weekendcourse.ui.list.PlaceListViewModel
import com.pinkanimal.weekendcourse.ui.util.KakaoMapHelper
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

@Composable
fun CourseScreen(
    viewModel: PlaceListViewModel = hiltViewModel()
) {
    val places by viewModel.places.collectAsState()
    val context = LocalContext.current

    if (places.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "아직 코스가 없어요",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "장소를 더 저장해보세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    // Split places into Saturday (first half) and Sunday (second half) groups
    val saturdayPlaces: List<PlaceEntity>
    val sundayPlaces: List<PlaceEntity>
    if (places.size <= 1) {
        saturdayPlaces = places
        sundayPlaces = emptyList()
    } else {
        val mid = (places.size + 1) / 2
        saturdayPlaces = places.subList(0, mid)
        sundayPlaces = places.subList(mid, places.size)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "주말 코스",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (saturdayPlaces.isNotEmpty()) {
            item {
                CourseDayCard(
                    dayLabel = "토요일",
                    places = saturdayPlaces,
                    onNavigate = { place -> KakaoMapHelper.openNavigation(context, place) }
                )
            }
        }

        if (sundayPlaces.isNotEmpty()) {
            item {
                CourseDayCard(
                    dayLabel = "일요일",
                    places = sundayPlaces,
                    onNavigate = { place -> KakaoMapHelper.openNavigation(context, place) }
                )
            }
        }
    }
}

@Composable
private fun CourseDayCard(
    dayLabel: String,
    places: List<PlaceEntity>,
    onNavigate: (PlaceEntity) -> Unit
) {
    // Estimate total cost from priceHint (extract first number if possible)
    val totalCostEstimate = places.mapNotNull { place ->
        place.priceHint?.let { hint ->
            Regex("\\d[\\d,]*").find(hint.replace(",", ""))?.value?.toLongOrNull()
        }
    }.sum().takeIf { it > 0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (totalCostEstimate != null) {
                    Text(
                        text = "예상 비용: ~${"%,d".format(totalCostEstimate)}원",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            places.forEach { place ->
                CoursePlaceRow(place = place, onNavigate = { onNavigate(place) })
            }
        }
    }
}

@Composable
private fun CoursePlaceRow(
    place: PlaceEntity,
    onNavigate: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = place.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = onNavigate,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "길안내",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
