package com.heicos.utils.manager

interface CosplayDownloader {
    fun downloadFile(url: String, name: String = ""): Long
}