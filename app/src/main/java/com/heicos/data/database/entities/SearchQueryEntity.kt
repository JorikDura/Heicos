package com.heicos.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchQueryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String
)
