package com.heicos.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.heicos.domain.model.SearchQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchQueryDao {
    @Upsert
    suspend fun upsertSearchQuery(query: SearchQuery)

    @Query("DELETE FROM searchquery")
    suspend fun deleteAllSearchQueries()

    @Delete
    suspend fun deleteById(searchItem: SearchQuery)

    @Query("SELECT * FROM searchquery ORDER BY id ASC")
    fun getSearchQueries(): Flow<List<SearchQuery>>

}