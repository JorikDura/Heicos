package com.heicos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.data.database.entities.SearchQueryEntity

@Database(
    entities = [SearchQueryEntity::class, CosplayPreviewEntity::class],
    version = 1
)
abstract class CosplaysDataBase : RoomDatabase() {

    abstract val searchDao: SearchQueryDao
    abstract val cosplayDao: CosplayPreviewDao

}