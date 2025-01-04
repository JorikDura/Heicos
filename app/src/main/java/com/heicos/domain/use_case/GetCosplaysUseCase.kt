package com.heicos.domain.use_case

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.repository.CosplayRepository
import com.heicos.domain.util.CosplayType
import com.heicos.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCosplaysUseCase @Inject constructor(
    private val repository: CosplayRepository
) {

    suspend operator fun invoke(
        page: Int = 1,
        cosplayType: CosplayType,
        showDownloaded: Boolean
    ): Flow<Resource<List<CosplayPreview>>> {
        return repository.getCosplays(page, cosplayType, showDownloaded)
    }

}