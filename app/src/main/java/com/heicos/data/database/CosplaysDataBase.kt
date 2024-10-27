package com.heicos.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.data.database.entities.SearchQueryEntity

@Database(
    entities = [SearchQueryEntity::class, CosplayPreviewEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
abstract class CosplaysDataBase : RoomDatabase() {

    abstract val searchDao: SearchQueryDao
    abstract val cosplayDao: CosplayPreviewDao

}