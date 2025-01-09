package com.heicos.utils.manager

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.heicos.presentation.util.USER_AGENT_MOZILLA
import javax.inject.Inject

class CosplayDownloaderImpl @Inject constructor(
    context: Context
) : CosplayDownloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override suspend fun downloadFile(url: String, name: String) {
        val fileType = url.reversed().substringBefore("/").reversed()
        val id = url.reversed().substringAfter("/").substringBefore("/").reversed()
        var fileName = if (name.isNotEmpty()) {
            "${name}_${id}_${fileType}".replace("/", "-")
        } else {
            val date = if (url.contains("video")) {
                url.substringAfter("video/").substringBefore("/")
            } else {
                url.substringAfter("upload/").substringBefore("/")
            }
            "${id}_${date}_${fileType}"
        }

        fileName = fileName.replace(
            regex = Regex("['\"\\[\\]]*"),
            replacement = ""
        )

        fileName = fileName.replace(
            oldValue = " ",
            newValue = "_"
        )

        val request = DownloadManager.Request(url.toUri())
            .addRequestHeader("User-Agent", USER_AGENT_MOZILLA)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Heicos/$fileName")

        downloadManager.enqueue(request)
    }

    override suspend fun downloadFiles(urls: List<String>, name: String) {
        TODO("Not yet implemented")
    }
}