package com.heicos.presentation.cosplays.ranking_cosplays

import com.heicos.domain.model.CosplayPreview

data class RankingCosplaysState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val isRefreshing: Boolean = false,
    val nextDataIsLoading: Boolean = false,
    val nextDataIsEmpty: Boolean = false,
    val cosplays: List<CosplayPreview> = emptyList()
)
