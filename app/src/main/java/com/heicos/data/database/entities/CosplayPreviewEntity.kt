package com.heicos.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.heicos.domain.util.CosplayMediaType

@Entity(indices = [Index(value = ["name"], unique = false)])
data class CosplayPreviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "downloaded_at") val downloadedAt: Long?,
    @ColumnInfo(defaultValue = "") val url: String,
    @ColumnInfo(name = "preview_url", defaultValue = "") val previewUrl: String,
    @ColumnInfo(name = "story_page_url", defaultValue = "") val storyPageUrl: String,
    @ColumnInfo(name = "viewed_at") val viewedAt: Long?,
    @ColumnInfo(name = "type", defaultValue = CosplayMediaType.IMAGES) val type: String
)