package com.heicos.domain.util

sealed class CosplayType {
    data object Recently : CosplayType()
    data object New : CosplayType()
    data object Ranking : CosplayType()
    data class Search(val query: String) : CosplayType()
    data object RecentlyViewed : CosplayType()
    data object NewVideo : CosplayType()
}