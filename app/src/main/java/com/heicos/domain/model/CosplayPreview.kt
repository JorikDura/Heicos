package com.heicos.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CosplayPreview(
    val id: Int = 0,
    val pageUrl: String = "",
    val storyPageUrl: String = "",
    val previewUrl: String = "",
    val title: String = "",
    val date: String = "",
    val isDownloaded: Boolean = false,
    val downloadTime: String? = null
) : Parcelable
