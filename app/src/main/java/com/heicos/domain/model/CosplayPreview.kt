package com.heicos.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CosplayPreview(
    var id: Int = 0,
    val pageUrl: String = "",
    val storyPageUrl: String = "",
    val previewUrl: String = "",
    val title: String = "",
    val date: String = "",
    var isDownloaded: Boolean = false,
    var downloadTime: String? = null
) : Parcelable
