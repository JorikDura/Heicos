package com.heicos.presentation.cosplays.new_cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.data.database.SearchQueryDao
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.use_case.GetCosplaysUseCase
import com.heicos.domain.util.CosplayType
import com.heicos.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _state = MutableStateFlow(NewCosplaysScreenState())
    val state = _state.asStateFlow()

    init {
        loadNextCosplays()

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
                _state.value = _state.value.copy(isRefreshing = true)
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

            is NewCosplaysScreenEvents.DeleteSearchItem -> {
                viewModelScope.launch {
                    searchQueryDao.deleteById(event.searchItem)
                }
            }

            NewCosplaysScreenEvents.LoadSearchQueries -> {
                loadSearchQueries()
            }
        }
    }

    private fun loadNextCosplays(loadingNextData: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteData = getCosplaysUseCase(
                page = currentPage,
                cosplayType = if (isSearching) CosplayType.Search(searchQuery) else CosplayType.New
            )

            remoteData.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.value = _state.value.copy(message = result.message)
                    }

                    is Resource.Loading -> {
                        if (loadingNextData && result.isLoading) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                nextDataIsLoading = true,
                                cosplays = cosplaysCache,
                                message = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        result.data?.let { data ->
                            _state.value = if (data.isNotEmpty()) {
                                cosplaysCache.addAll(data)
                                _state.value.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    nextDataIsLoading = false,
                                    cosplays = cosplaysCache,
                                    message = null
                                )
                            } else {
                                _state.value.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isEmpty = cosplaysCache.isEmpty(),
                                    nextDataIsLoading = false,
                                    nextDataIsEmpty = true,
                                    cosplays = cosplaysCache,
                                    message = null
                                )
                            }
                        }
                        currentPage++
                    }
                }
            }
        }
    }

    private fun resetValues(isRefreshing: Boolean = false) {
        if (gridState.isScrollInProgress) {
            viewModelScope.launch { gridState.stopScroll() }
        }

        _state.value = _state.value.copy(
            isLoading = true,
            isEmpty = false,
            nextDataIsEmpty = false,
            message = null
        )

        if (!isRefreshing) {
            isSearching = false
            searchQuery = ""
        }

        gridState = LazyGridState()
        cosplaysCache.clear()
        currentPage = 1
    }

    private fun loadSearchQueries() {
        viewModelScope.launch {
            val queries = searchQueryDao.getSearchQueries()
            queries.collect { searchQuery ->
                if (_state.value.history != searchQuery) {
                    _state.value = _state.value.copy(
                        isHistoryLoading = false,
                        history = searchQuery
                    )
                }
            }
        }
    }
}