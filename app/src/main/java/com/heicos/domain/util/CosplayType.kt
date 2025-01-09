package com.heicos.domain.util

sealed class CosplayType {
    data object Recently : CosplayType()
    data object New : CosplayType()
    data object Ranking : CosplayType()
    data class Search(val query: String) : CosplayType()
    data object NewVideo : CosplayType()
    data object RankingVideo : CosplayType()
    data object RecentlyViewed : CosplayType()
    data object RecentlyAsian : CosplayType()
    data object NewAsian : CosplayType()
    data object RankingAsian : CosplayType()
}