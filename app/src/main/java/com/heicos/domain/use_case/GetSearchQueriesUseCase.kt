package com.heicos.domain.use_case

import com.heicos.domain.model.SearchQuery
import com.heicos.domain.repository.CosplayRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchQueriesUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(): Flow<List<SearchQuery>> {
        return repository.getSearchQueries()
    }

}