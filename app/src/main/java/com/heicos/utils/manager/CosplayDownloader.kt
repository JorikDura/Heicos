package com.heicos.utils.manager

interface CosplayDownloader {
    suspend fun downloadFile(url: String, name: String = ""): Long
}