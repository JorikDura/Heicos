package com.heicos.data.mapper

import com.heicos.data.database.entities.SearchQueryEntity
import com.heicos.domain.model.SearchQuery

fun SearchQueryEntity.toSearchQuery(): SearchQuery {
    return SearchQuery(
        id = id,
        query = query
    )
}

fun SearchQuery.toSearchQueryEntity(): SearchQueryEntity {
    return SearchQueryEntity(
        id = id,
        query = query
    )
}