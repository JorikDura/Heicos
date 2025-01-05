package com.heicos.data.database

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface BackupDao {
    @RawQuery
    fun rawQuery(query: SupportSQLiteQuery): Boolean?
}