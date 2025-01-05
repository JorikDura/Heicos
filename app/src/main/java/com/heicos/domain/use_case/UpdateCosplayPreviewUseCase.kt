package com.heicos.domain.use_case

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import javax.inject.Inject

class UpdateCosplayPreviewUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(
        cosplayPreview: CosplayPreview,
        time: Long,
        isDownloaded: Boolean = false
    ) {
        return repository.updateCosplayPreview(cosplayPreview, time, isDownloaded)
    }

}