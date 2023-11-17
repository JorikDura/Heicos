package com.heicos.presentation.cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.data.database.SearchQueryDao
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.use_case.GetCosplaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CosplaysScreenViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase,
    private val searchQueryDao: SearchQueryDao
) : ViewModel() {

    private var page = 1
    private var cosplaysCache = mutableListOf<CosplayPreview>()

    private val _historySearch = mutableStateListOf<SearchQuery>()
    val historySearch: List<SearchQuery> = _historySearch

    var searchQuery = ""
    var gridState = LazyGridState()
    var screenState by mutableStateOf(CosplaysScreenState(isLoading = true))

    init {
        loadNextData()
        viewModelScope.launch {
            val queries = searchQueryDao.getSearchQueries()
            queries.collect { searchQuery ->
                searchQuery.forEach { query ->
                    if (!_historySearch.contains(query)) {
                        _historySearch.add(query)
                    }
                }
            }
        }
    }

    fun onEvent(event: CosplaysScreenEvents) {
        when (event) {
            CosplaysScreenEvents.LoadNextData -> {
                loadNextData(true)
            }

            CosplaysScreenEvents.Reset -> {
                if (searchQuery.isEmpty())
                    return

                resetValues()
                loadNextData()
            }

            is CosplaysScreenEvents.Search -> {
                resetValues()
                searchQuery = event.query
                loadNextData()
            }

            is CosplaysScreenEvents.AddHistoryQuery -> {
                val searchQuery = SearchQuery(query = event.query)
                viewModelScope.launch {
                    searchQueryDao.upsertSearchQuery(searchQuery)
                }
            }

            is CosplaysScreenEvents.DeleteHistoryQuery -> {
                viewModelScope.launch {
                    searchQueryDao.deleteAllSearchQueries()
                }
                _historySearch.clear()
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