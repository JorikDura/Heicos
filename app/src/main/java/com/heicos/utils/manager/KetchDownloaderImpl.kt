package com.heicos.utils.manager

import android.os.Environment
import com.heicos.presentation.util.USER_AGENT_MOZILLA
import com.ketch.Ketch
import javax.inject.Inject

class KetchDownloaderImpl @Inject constructor(
    private val ketch: Ketch
) : CosplayDownloader {
    override fun downloadFile(url: String, name: String): Long {
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

        ketch.download(
            url = url,
            fileName = fileName,
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/Heicos/",
            tag = "Image",
            headers = hashMapOf(
                Pair("User-Agent", USER_AGENT_MOZILLA)
            )
        )

        return 0
    }
}