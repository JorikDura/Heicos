package com.heicos.utils.manager

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import javax.inject.Inject

class CosplayDownloaderImpl @Inject constructor(
    context: Context
) : CosplayDownloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String, name: String): Long {
        val fileType = url.reversed().substringBefore("/").reversed()
        val id = url.reversed().substringAfter("/").substringBefore("/").reversed()
        val fileName = if (name.isNotEmpty()) {
            "${name}_${id}_${fileType}".replace("/", "-")
        } else {
            val date = if (url.contains("video")) {
                url.substringAfter("video/").substringBefore("/")
            } else {
                url.substringAfter("upload/").substringBefore("/")
            }
            "${id}_${date}_${fileType}"
        }

        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "Heicos/$fileName")

        return downloadManager.enqueue(request)
    }
}