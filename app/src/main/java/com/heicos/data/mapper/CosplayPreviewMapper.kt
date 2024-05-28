package com.heicos.data.mapper

import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.domain.model.CosplayPreview

fun CosplayPreview.toCosplayPreviewEntity(time: Long): CosplayPreviewEntity {
    return CosplayPreviewEntity(
        id = id,
        name = title,
        createdAt = time
    )
}