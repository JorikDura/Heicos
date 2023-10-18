package com.heicos.domain.use_case

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class GetCosplaysUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(page: Int = 1, query: String = ""): List<CosplayPreview> {
        return repository.getCosplays(page, query)
    }

}