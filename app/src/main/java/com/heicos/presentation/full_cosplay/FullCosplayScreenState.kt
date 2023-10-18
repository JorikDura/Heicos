package com.heicos.presentation.full_cosplay

data class FullCosplayScreenState(
    val isLoading: Boolean = false,
    val cosplaysPhotoUrl: List<String> = emptyList()
)