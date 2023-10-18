package com.heicos.domain.manager

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import javax.inject.Inject

class CosplayDownloaderImpl @Inject constructor(
    context: Context
) : CosplayDownloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun downloadFile(url: String): Long {
        val date = if (url.contains("video")) {
            url.substringAfter("video/").substringBefore("/")
        } else {
            url.substringAfter("upload/").substringBefore("/")
        }
        val id = url.reversed().substringAfter("/").substringBefore("/").reversed()
        val fileType = url.reversed().substringBefore("/").reversed()
        val fileName = "${id}_${date}_${fileType}"

        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName)

        return downloadManager.enqueue(request)
    }
}