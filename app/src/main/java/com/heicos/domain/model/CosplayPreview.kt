package com.heicos.domain.model

import android.os.Parcelable
import com.heicos.domain.util.CosplayMediaType
import kotlinx.parcelize.Parcelize

@Parcelize
data class CosplayPreview(
    var id: Long = 0,
    val pageUrl: String = "",
    val storyPageUrl: String = "",
    val previewUrl: String = "",
    val title: String = "",
    val date: String = "",
    var isDownloaded: Boolean = false,
    var downloadTime: String? = null,
    var downloadedAt: Long? = null,
    var isViewed: Boolean = false,
    var type: CosplayMediaType = CosplayMediaType.Images
) : Parcelable
