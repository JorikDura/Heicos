package com.heicos.domain.repository

import com.heicos.domain.model.CosplayPreview

interface CosplayRepository {
    suspend fun getCosplays(page: Int): List<CosplayPreview>
    suspend fun getFullCosplay(url: String): List<String>

}