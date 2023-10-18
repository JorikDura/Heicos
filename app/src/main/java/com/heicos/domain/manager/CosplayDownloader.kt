package com.heicos.domain.manager

interface CosplayDownloader {
    fun downloadFile(url: String): Long
}