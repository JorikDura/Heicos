package com.heicos.presentation.cosplays

import com.heicos.domain.model.CosplayPreview

data class CosplaysScreenState(
    val isLoading: Boolean = false,
    val nextDataIsLoading: Boolean = false,
    val cosplays: List<CosplayPreview> = emptyList()
)