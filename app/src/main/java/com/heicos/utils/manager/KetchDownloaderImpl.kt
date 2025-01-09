package com.heicos.utils.manager

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import com.heicos.presentation.util.USER_AGENT_MOZILLA
import com.ketch.Ketch
import com.ketch.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class KetchDownloaderImpl @Inject constructor(
    private val ketch: Ketch,
    private val context: Context
) : CosplayDownloader {
    override suspend fun downloadFile(url: String, name: String): Long {
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

        val download = ketch.download(
            url = url,
            fileName = fileName,
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/Heicos/",
            tag = "Image",
            headers = hashMapOf(
                Pair("User-Agent", USER_AGENT_MOZILLA)
            )
        )

        ketch.observeDownloadById(download)
            .flowOn(Dispatchers.IO)
            .collect { downloadedModel ->
                when (downloadedModel.status) {
                    Status.SUCCESS -> {
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/Heicos/"),
                            null,
                            null
                        )
                    }

                    else -> Unit
                }
            }

        return 0
    }
}