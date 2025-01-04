package com.heicos.domain.repository

interface BackupRepository {
    fun import(tableName: String)
    fun export(tableName: String)
}