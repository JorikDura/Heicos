package com.heicos.domain.repository

import com.heicos.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BackupRepository {
    fun import(tableName: String): Flow<Resource<String>>
    fun export(tableName: String): Flow<Resource<String>>
    fun truncate(tableName: String): Flow<Resource<String>>
}