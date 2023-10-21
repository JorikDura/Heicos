package com.heicos.presentation.cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.GetCosplaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CosplaysScreenViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase
) : ViewModel() {

    private var page = 1
    private var cosplaysCache = mutableListOf<CosplayPreview>()

    private val _historySearch = mutableListOf<String>()
    val historySearch: List<String> = _historySearch

    var searchQuery = ""
    var gridState = LazyGridState()
    var screenState by mutableStateOf(CosplaysScreenState(isLoading = true))

    init {
        loadNextData()
    }

    fun onEvent(event: CosplaysScreenEvents) {
        when (event) {
            CosplaysScreenEvents.LoadNextData -> {
                loadNextData(true)
            }

            CosplaysScreenEvents.Reset -> {
                resetValues()
                loadNextData()
            }

            is CosplaysScreenEvents.Search -> {
                resetValues()
                searchQuery = event.query
                loadNextData()
            }

            is CosplaysScreenEvents.AddHistoryQuery -> {
                _historySearch.add(event.query)
            }
        }
    }

    private fun loadNextData(loadingNextData: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loadingNextData) {
                screenState = screenState.copy(
                    isLoading = false,
                    nextDataIsLoading = true,
                    cosplays = cosplaysCache
                )
            }

            val result = getCosplaysUseCase(page, searchQuery)

            screenState = if (result.isNotEmpty()) {
                cosplaysCache.addAll(result)
                screenState.copy(
                    isLoading = false,
                    nextDataIsLoading = false,
                    cosplays = cosplaysCache
                )
            } else {
                screenState.copy(
                    isLoading = false,
                    isEmpty = cosplaysCache.isEmpty(),
                    nextDataIsLoading = false,
                    nextDataIsEmpty = true,
                    cosplays = cosplaysCache
                )
            }

            page++
        }
    }

    private fun resetValues() {
        if (gridState.isScrollInProgress) {
            viewModelScope.launch { gridState.stopScroll() }
        }
        gridState = LazyGridState()
        cosplaysCache.clear()
        searchQuery = ""
        page = 1
        screenState = screenState.copy(
            isLoading = true,
            isEmpty = false,
            nextDataIsEmpty = false
        )
    }
}