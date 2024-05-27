package com.heicos.domain.model

data class SearchQuery(
    val id: Int = UNDEFINED_ID,
    val query: String
) {
    private companion object {
        const val UNDEFINED_ID = 0
    }

}
