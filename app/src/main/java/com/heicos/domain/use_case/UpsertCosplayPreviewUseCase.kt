package com.heicos.domain.use_case

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class UpsertCosplayPreviewUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(cosplayPreview: CosplayPreview, time: Long) {
        repository.upsertCosplayPreview(cosplayPreview, time)
    }

}