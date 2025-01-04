package com.heicos.presentation.cosplays

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.util.CosplayType

data class CosplaysScreenState(
    val currentCosplayType: CosplayType = CosplayType.New,
    val currentPage: Int = CosplaysScreenViewModel.DEFAULT_PAGE,
    val nextPage: Int = CosplaysScreenViewModel.DEFAULT_PAGE,
    val lastPage: Int? = null,
    val isLoading: Boolean = false,
    val isHistoryLoading: Boolean = true,
    val isHistoryIsEmpty: Boolean = false,
    val isEmpty: Boolean = false,
    val isRefreshing: Boolean = false,
    val nextDataIsLoading: Boolean = false,
    val nextDataIsEmpty: Boolean = false,
    val cosplays: List<CosplayPreview> = emptyList(),
    val history: List<SearchQuery> = emptyList(),
    val message: String? = null,
    val nextDataMessage: String? = null,
    val reversedMode: Boolean = false,
    val showDownloaded: Boolean = true
)