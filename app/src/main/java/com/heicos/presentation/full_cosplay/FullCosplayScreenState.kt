package com.heicos.presentation.full_cosplay

data class FullCosplayScreenState(
    val isLoading: Boolean = false,
    val tagsIsLoading: Boolean = true,
    val cosplaysPhotoUrl: List<String> = emptyList(),
    val cosplayTags: List<String> = emptyList(),
    val message: String? = null,
    val messageInMoreInfo: String? = null
)