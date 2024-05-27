package com.heicos.domain.use_case

import com.heicos.domain.model.SearchQuery
import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class DeleteAllSearchQueriesUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke() {
        return repository.deleteAllSearchQueries()
    }

}