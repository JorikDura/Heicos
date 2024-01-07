package com.heicos.domain.repository

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.util.CosplayType

interface CosplayRepository {
    suspend fun getCosplays(page: Int, cosplayType: CosplayType): List<CosplayPreview>
    suspend fun getFullCosplay(url: String): List<String>

}