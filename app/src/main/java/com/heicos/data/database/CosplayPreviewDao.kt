package com.heicos.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.heicos.data.database.entities.CosplayPreviewEntity

@Dao
interface CosplayPreviewDao {
    @Upsert
    suspend fun upsertCosplayPreview(cosplayPreviewEntity: CosplayPreviewEntity)

    @Query("SELECT * FROM CosplayPreviewEntity where name IN (:names) LIMIT 20")
    suspend fun getCosplayPreviews(names: List<String>): List<CosplayPreviewEntity>
}