package com.heicos.presentation.full_video_cosplay

import com.heicos.domain.model.CosplayPreview

data class FullVideoCosplayState(
    val cosplayPreview: CosplayPreview = CosplayPreview(),
    val videoUrl: String = "",
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isDownloadedSuccessful: Boolean = false
)
