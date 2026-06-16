package com.pinkanimal.weekendcourse.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinkanimal.weekendcourse.data.local.PlaceEntity
import com.pinkanimal.weekendcourse.data.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceListViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    val places: StateFlow<List<PlaceEntity>> = placeRepository.getAllPlaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deletePlace(entity: PlaceEntity) {
        viewModelScope.launch {
            placeRepository.deletePlace(entity)
        }
    }
}
