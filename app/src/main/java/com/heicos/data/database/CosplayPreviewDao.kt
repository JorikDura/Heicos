package com.heicos.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.heicos.data.database.entities.CosplayPreviewEntity

@Dao
interface CosplayPreviewDao {
    @Upsert
    suspend fun upsertCosplayPreview(cosplayPreviewEntity: CosplayPreviewEntity)

    @Query("SELECT * FROM CosplayPreviewEntity")
    suspend fun getCosplayPreviews(): List<CosplayPreviewEntity>
}