package com.heicos.presentation.cosplays.new_cosplays

import com.heicos.domain.model.SearchQuery

sealed class NewCosplaysScreenEvents {
    data class Search(val query: String) : NewCosplaysScreenEvents()
    data class AddHistoryQuery(val query: String) : NewCosplaysScreenEvents()
    data class DeleteSearchItem(val searchItem: SearchQuery) : NewCosplaysScreenEvents()
    data object Reset : NewCosplaysScreenEvents()
    data object Refresh : NewCosplaysScreenEvents()
    data object LoadNextData : NewCosplaysScreenEvents()
    data object DeleteHistoryQuery : NewCosplaysScreenEvents()
}