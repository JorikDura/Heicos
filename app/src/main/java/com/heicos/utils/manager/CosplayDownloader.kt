package com.heicos.utils.manager

interface CosplayDownloader {
    suspend fun downloadFile(url: String, name: String = "")
    suspend fun downloadFiles(urls: List<String>, name: String = "")
}