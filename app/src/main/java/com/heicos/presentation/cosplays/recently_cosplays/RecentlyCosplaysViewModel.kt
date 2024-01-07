package com.heicos.presentation.cosplays.recently_cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.GetCosplaysUseCase
import com.heicos.domain.util.CosplayType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyCosplaysViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase
) : ViewModel() {
    private var currentPage = 1
    private val cosplaysCache = mutableListOf<CosplayPreview>()

    var gridState = LazyGridState()

    var state by mutableStateOf(RecentlyCosplaysState(isLoading = true))

    init {
        loadNextCosplays()
    }

    fun onEvent(event: RecentlyCosplaysEvents) {
        when (event) {
            RecentlyCosplaysEvents.LoadNextData -> {
                loadNextCosplays(true)
            }

            RecentlyCosplaysEvents.Refresh -> {
                state = state.copy(isRefreshing = true)
                resetValues()
                loadNextCosplays()
            }
        }
    }

    private fun loadNextCosplays(loadingNextData: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loadingNextData) {
                state = state.copy(
                    isLoading = false,
                    nextDataIsLoading = true,
                    cosplays = cosplaysCache
                )
            }

            val result = getCosplaysUseCase(
                page = currentPage,
                cosplayType = CosplayType.Recently
            )

            state = if (result.isNotEmpty()) {
                cosplaysCache.addAll(result)
                state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    nextDataIsLoading = false,
                    cosplays = cosplaysCache
                )
            } else {
                state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isEmpty = cosplaysCache.isEmpty(),
                    nextDataIsLoading = false,
                    nextDataIsEmpty = true,
                    cosplays = cosplaysCache
                )
            }

            currentPage++
        }
    }

    private fun resetValues() {
        if (gridState.isScrollInProgress) {
            viewModelScope.launch { gridState.stopScroll() }
        }

        state = state.copy(
            isLoading = true,
            isEmpty = false,
            nextDataIsEmpty = false
        )
        gridState = LazyGridState()
        cosplaysCache.clear()
        currentPage = 1
    }
}