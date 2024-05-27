package com.heicos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heicos.data.database.entities.SearchQueryEntity

@Database(
    entities = [SearchQueryEntity::class],
    version = 1
)
abstract class SearchQueryDataBase : RoomDatabase() {

    abstract val dao: SearchQueryDao

}