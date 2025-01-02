package com.heicos.data.mapper

import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.domain.model.CosplayPreview

fun CosplayPreview.toCosplayPreviewEntity(
    time: Long?,
    isDownloaded: Boolean
): CosplayPreviewEntity {
    return CosplayPreviewEntity(
        id = id,
        name = title,
        downloadedAt = if (isDownloaded) time else null,
        url = pageUrl,
        previewUrl = previewUrl,
        storyPageUrl = storyPageUrl,
        viewedAt = time
    )
}