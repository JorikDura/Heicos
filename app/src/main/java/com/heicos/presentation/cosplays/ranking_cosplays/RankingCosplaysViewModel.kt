package com.heicos.presentation.cosplays.ranking_cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.GetCosplaysUseCase
import com.heicos.domain.util.CosplayType
import com.heicos.presentation.cosplays.new_cosplays.NewCosplaysScreenState
import com.heicos.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingCosplaysViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase
) : ViewModel() {

    private var currentPage = 1
    private val cosplaysCache = mutableListOf<CosplayPreview>()

    var gridState = LazyGridState()

    private val _state = MutableStateFlow(NewCosplaysScreenState())
    val state = _state.asStateFlow()

    init {
        loadNextCosplays()
    }

    fun onEvent(event: RankingCosplaysEvents) {
        when (event) {
            RankingCosplaysEvents.LoadNextData -> {
                loadNextCosplays(true)
            }

            RankingCosplaysEvents.Refresh -> {
                _state.value = _state.value.copy(isRefreshing = true)
                resetValues()
                loadNextCosplays()
            }
        }
    }

    private fun loadNextCosplays(loadingNextData: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loadingNextData) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    nextDataIsLoading = true,
                    cosplays = cosplaysCache,
                    message = null
                )
            }

            val remoteData = getCosplaysUseCase(
                page = currentPage,
                cosplayType = CosplayType.Ranking
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

    private fun resetValues() {
        if (gridState.isScrollInProgress) {
            viewModelScope.launch { gridState.stopScroll() }
        }

        _state.value = state.value.copy(
            isLoading = true,
            isEmpty = false,
            nextDataIsEmpty = false,
            message = null
        )
        gridState = LazyGridState()
        cosplaysCache.clear()
        currentPage = 1
    }
}