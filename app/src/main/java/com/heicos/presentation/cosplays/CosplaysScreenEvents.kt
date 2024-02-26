package com.heicos.presentation.cosplays

import com.heicos.domain.model.SearchQuery
import com.heicos.domain.util.CosplayType

sealed class CosplaysScreenEvents {
    data class Search(val query: String) : CosplaysScreenEvents()
    data class ChangeCosplayType(val type: CosplayType) : CosplaysScreenEvents()
    data class DeleteSearchItem(val searchItem: SearchQuery) : CosplaysScreenEvents()
    data class ChangePage(val page: Int) : CosplaysScreenEvents()
    data object Reset : CosplaysScreenEvents()
    data object Refresh : CosplaysScreenEvents()
    data object LoadNextData : CosplaysScreenEvents()
    data object DeleteHistoryQuery : CosplaysScreenEvents()
    data object LoadSearchQueries : CosplaysScreenEvents()
}