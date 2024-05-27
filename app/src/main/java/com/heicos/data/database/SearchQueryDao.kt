package com.heicos.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.heicos.data.database.entities.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchQueryDao {
    @Upsert
    suspend fun upsertSearchQuery(searchItem: SearchQueryEntity)

    @Query("DELETE FROM searchqueryentity")
    suspend fun deleteAllSearchQueries()

    @Delete
    suspend fun deleteById(searchItem: SearchQueryEntity)

    @Query("SELECT * FROM searchqueryentity ORDER BY id ASC")
    fun getSearchQueries(): Flow<List<SearchQueryEntity>>

}