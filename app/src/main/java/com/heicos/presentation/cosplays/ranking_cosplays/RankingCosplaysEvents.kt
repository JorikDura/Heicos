package com.heicos.presentation.cosplays.ranking_cosplays

sealed class RankingCosplaysEvents {
    data object Refresh : RankingCosplaysEvents()
    data object LoadNextData : RankingCosplaysEvents()
}