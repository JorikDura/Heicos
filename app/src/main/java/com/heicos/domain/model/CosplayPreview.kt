package com.heicos.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CosplayPreview(
    val pageUrl: String = "",
    val storyPageUrl: String = "",
    val previewUrl: String = "",
    val title: String = "",
    val date: String = ""
) : Parcelable
