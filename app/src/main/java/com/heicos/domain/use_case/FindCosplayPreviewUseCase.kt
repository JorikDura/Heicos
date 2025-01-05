package com.heicos.domain.use_case

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class FindCosplayPreviewUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(url: String): CosplayPreview? {
        return repository.findCosplayPreview(url)
    }

}