package com.heicos.presentation.cosplays

sealed class CosplaysScreenEvents {

    data class Search(val query: String): CosplaysScreenEvents()
    data object Reset: CosplaysScreenEvents()
    data object LoadNextData: CosplaysScreenEvents()

}
