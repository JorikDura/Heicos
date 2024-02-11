package com.heicos.domain.repository

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.util.CosplayType
import com.heicos.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CosplayRepository {
    suspend fun getCosplays(
        page: Int,
        cosplayType: CosplayType
    ): Flow<Resource<List<CosplayPreview>>>

    suspend fun getFullCosplay(url: String): Flow<Resource<List<String>>>
    suspend fun getCosplayTags(url: String): Flow<Resource<List<String>>>

}