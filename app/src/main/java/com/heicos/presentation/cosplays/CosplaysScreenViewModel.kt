package com.heicos.presentation.cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.data.database.SearchQueryDao
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
class CosplaysScreenViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase,
    private val searchQueryDao: SearchQueryDao
) : ViewModel() {

    private var currentPage = 1
    private var isSearching = false

    var searchQuery = ""
    var gridState = LazyGridState()

    private val _state = MutableStateFlow(CosplaysScreenState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadNextCosplays()

    }

    fun onEvent(event: CosplaysScreenEvents) {
        when (event) {
            CosplaysScreenEvents.LoadNextData -> {
                loadNextCosplays(true)
            }

            CosplaysScreenEvents.Reset -> {
                if (searchQuery.isEmpty())
                    return

                resetValues()
                loadNextCosplays()
            }

            CosplaysScreenEvents.Refresh -> {
                _state.value = _state.value.copy(isRefreshing = true)
                resetValues(true)
                loadNextCosplays()
            }

            is CosplaysScreenEvents.Search -> {
                resetValues()
                searchQuery = event.query
                isSearching = true
                val queryContains = _state.value.history.find { searchQuery ->
                    searchQuery.query == event.query
                }
                if (queryContains == null) {
                    val searchQuery = SearchQuery(query = event.query)
                    viewModelScope.launch {
                        searchQueryDao.upsertSearchQuery(searchQuery)
                    }
                }
                loadNextCosplays()
            }

            is CosplaysScreenEvents.DeleteHistoryQuery -> {
                viewModelScope.launch {
                    searchQueryDao.deleteAllSearchQueries()
                }
            }

            is CosplaysScreenEvents.DeleteSearchItem -> {
                viewModelScope.launch {
                    searchQueryDao.deleteById(event.searchItem)
                }
            }

            is CosplaysScreenEvents.ChangeCosplayType -> {
                resetValues()
                _state.value = _state.value.copy(
                    currentCosplayType = event.type
                )
                loadNextCosplays()
            }

            CosplaysScreenEvents.LoadSearchQueries -> {
                loadSearchQueries()
            }
        }
    }

    private fun loadNextCosplays(loadingNextData: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteData = getCosplaysUseCase(
                page = currentPage,
                cosplayType = if (isSearching) CosplayType.Search(searchQuery) else _state.value.currentCosplayType
            )

            remoteData.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        if (_state.value.cosplays.isNotEmpty()) {
                            _state.value = _state.value.copy(nextDataMessage = result.message)
                        } else {
                            _state.value = _state.value.copy(message = result.message)
                        }
                    }

                    is Resource.Loading -> {
                        if (loadingNextData && result.isLoading) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                nextDataIsLoading = true,
                                message = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        result.data?.let { data ->
                            _state.value = if (data.isNotEmpty()) {
                                _state.value.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    nextDataIsLoading = false,
                                    cosplays = _state.value.cosplays + data,
                                    message = null
                                )
                            } else {
                                _state.value.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isEmpty = _state.value.cosplays.isEmpty(),
                                    nextDataIsLoading = false,
                                    nextDataIsEmpty = true,
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
            message = null,
            nextDataMessage = null,
            cosplays = emptyList()
        )

        if (!isRefreshing) {
            isSearching = false
            searchQuery = ""
        }

        gridState = LazyGridState()
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