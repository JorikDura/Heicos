package com.heicos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heicos.domain.model.SearchQuery

@Database(
    entities = [SearchQuery::class],
    version = 1
)
abstract class SearchQueryDataBase : RoomDatabase() {

    abstract val dao: SearchQueryDao

}