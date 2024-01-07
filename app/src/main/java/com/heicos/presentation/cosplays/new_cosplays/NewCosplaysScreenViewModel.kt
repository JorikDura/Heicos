package com.heicos.presentation.cosplays.new_cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.data.database.SearchQueryDao
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.use_case.GetCosplaysUseCase
import com.heicos.domain.util.CosplayType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCosplaysScreenViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase,
    private val searchQueryDao: SearchQueryDao
) : ViewModel() {

    private var currentPage = 1
    private val cosplaysCache = mutableListOf<CosplayPreview>()
    private var isSearching = false

    var searchQuery = ""
    var gridState = LazyGridState()

    var state by mutableStateOf(NewCosplaysScreenState(isLoading = true))

    init {
        loadNextCosplays()
        viewModelScope.launch {
            val queries = searchQueryDao.getSearchQueries()
            queries.collect { searchQuery ->
                if (state.history != searchQuery) {
                    state = state.copy(
                        history = searchQuery
                    )
                }
            }
        }
    }

    fun onEvent(event: NewCosplaysScreenEvents) {
        when (event) {
            NewCosplaysScreenEvents.LoadNextData -> {
                loadNextCosplays(true)
            }

            NewCosplaysScreenEvents.Reset -> {
                if (searchQuery.isEmpty())
                    return

                resetValues()
                loadNextCosplays()
            }

            NewCosplaysScreenEvents.Refresh -> {
                state = state.copy(isRefreshing = true)
                resetValues(true)
                loadNextCosplays()
            }

            is NewCosplaysScreenEvents.Search -> {
                resetValues()
                searchQuery = event.query
                isSearching = true
                loadNextCosplays()
            }

            is NewCosplaysScreenEvents.AddHistoryQuery -> {
                val searchQuery = SearchQuery(query = event.query)
                viewModelScope.launch {
                    searchQueryDao.upsertSearchQuery(searchQuery)
                }
            }

            is NewCosplaysScreenEvents.DeleteHistoryQuery -> {
                viewModelScope.launch {
                    searchQueryDao.deleteAllSearchQueries()
                }
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
                cosplayType = if (isSearching) CosplayType.Search(searchQuery) else CosplayType.New
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

    private fun resetValues(isRefreshing: Boolean = false) {
        if (gridState.isScrollInProgress) {
            viewModelScope.launch { gridState.stopScroll() }
        }

        state = state.copy(
            isLoading = true,
            isEmpty = false,
            nextDataIsEmpty = false
        )

        if (!isRefreshing) {
            isSearching = false
            searchQuery = ""
        }

        gridState = LazyGridState()
        cosplaysCache.clear()
        currentPage = 1
    }
}