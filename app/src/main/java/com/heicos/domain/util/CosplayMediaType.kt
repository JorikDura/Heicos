package com.heicos.domain.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class CosplayMediaType : Parcelable {
    data object Images : CosplayMediaType()
    data object Video : CosplayMediaType()

    companion object {
        const val IMAGES = "images"
        const val VIDEO = "video"
    }
}