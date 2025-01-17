package com.heicos.domain.use_case

import com.heicos.domain.model.SearchQuery
import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class DeleteSearchQueryByIdUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(searchQuery: SearchQuery) {
        return repository.deleteSearchQueryById(searchQuery)
    }

}