package com.heicos.presentation.full_cosplay

sealed class FullCosplayScreenEvents {
    data object DownloadAllImages : FullCosplayScreenEvents()
    data class DownloadImage(val url: String) : FullCosplayScreenEvents()
    data class ScrollToItem(val index: Int) : FullCosplayScreenEvents()

}
