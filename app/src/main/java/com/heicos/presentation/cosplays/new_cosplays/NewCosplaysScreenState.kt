package com.heicos.presentation.cosplays.new_cosplays

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery

data class NewCosplaysScreenState(
    val isLoading: Boolean = false,
    val isHistoryLoading: Boolean = true,
    val isEmpty: Boolean = false,
    val isRefreshing: Boolean = false,
    val nextDataIsLoading: Boolean = false,
    val nextDataIsEmpty: Boolean = false,
    val cosplays: List<CosplayPreview> = emptyList(),
    val history: List<SearchQuery> = emptyList(),
    val message: String? = null,
    val nextDataMessage: String? = null
)