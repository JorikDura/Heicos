package com.heicos.data.mapper

import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.domain.model.CosplayPreview
import com.heicos.utils.time.convertTime

fun CosplayPreview.toCosplayPreviewEntity(
    time: Long?,
    isDownloaded: Boolean
): CosplayPreviewEntity {
    return CosplayPreviewEntity(
        id = id,
        name = title,
        downloadedAt = if (isDownloaded) time else this.downloadedAt,
        url = pageUrl,
        previewUrl = previewUrl,
        storyPageUrl = storyPageUrl,
        viewedAt = time
    )
}

fun CosplayPreviewEntity.toCosplayPreview(data: String = ""): CosplayPreview {
    return CosplayPreview(
        id = id,
        pageUrl = url,
        storyPageUrl = storyPageUrl,
        previewUrl = previewUrl,
        title = name,
        date = data,
        isDownloaded = downloadedAt != null,
        downloadedAt = downloadedAt,
        downloadTime = downloadedAt?.let { convertTime(it) },
        isViewed = viewedAt == null
    )
}