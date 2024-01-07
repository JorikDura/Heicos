package com.heicos.domain.use_case

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import com.heicos.domain.util.CosplayType
import javax.inject.Inject

class GetCosplaysUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(page: Int = 1, cosplayType: CosplayType): List<CosplayPreview> {
        return repository.getCosplays(page, cosplayType)
    }

}