package com.heicos.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.data.database.entities.SearchQueryEntity

@Database(
    entities = [SearchQueryEntity::class, CosplayPreviewEntity::class],
    version = 10,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, spec = CosplaysDataBase.Migration7To8::class),
        AutoMigration(from = 8, to = 9, spec = CosplaysDataBase.Migration8To9::class),
        AutoMigration(from = 9, to = 10),

    ],
    exportSchema = true
)

abstract class CosplaysDataBase : RoomDatabase() {

    abstract val searchDao: SearchQueryDao

    abstract val cosplayDao: CosplayPreviewDao

    abstract val backupDao: BackupDao

    @RenameColumn(
        tableName = "CosplayPreviewEntity",
        fromColumnName = "created_at",
        toColumnName = "downloaded_at"
    )
    class Migration7To8 : AutoMigrationSpec

    @DeleteColumn.Entries(
        DeleteColumn(
            tableName = "CosplayPreviewEntity",
            columnName = "is_viewed"
        ),
        DeleteColumn(
            tableName = "CosplayPreviewEntity",
            columnName = "is_downloaded"
        )
    )
    class Migration8To9 : AutoMigrationSpec
}