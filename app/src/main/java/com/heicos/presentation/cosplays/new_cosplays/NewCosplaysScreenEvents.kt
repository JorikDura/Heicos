package com.heicos.presentation.cosplays.new_cosplays

sealed class NewCosplaysScreenEvents {
    data class Search(val query: String) : NewCosplaysScreenEvents()
    data class AddHistoryQuery(val query: String) : NewCosplaysScreenEvents()
    data object Reset : NewCosplaysScreenEvents()
    data object Refresh : NewCosplaysScreenEvents()
    data object LoadNextData : NewCosplaysScreenEvents()
    data object DeleteHistoryQuery: NewCosplaysScreenEvents()
}