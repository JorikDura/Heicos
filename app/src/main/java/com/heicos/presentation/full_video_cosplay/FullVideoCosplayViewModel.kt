package com.heicos.presentation.full_video_cosplay

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import androidx.annotation.OptIn
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.SessionState
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.FindCosplayPreviewUseCase
import com.heicos.domain.use_case.GetFullVideoCosplayUseCase
import com.heicos.domain.use_case.InsertCosplayPreviewUseCase
import com.heicos.domain.use_case.UpdateCosplayPreviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@HiltViewModel
class FullVideoCosplayViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val getFullVideoCosplayUseCase: GetFullVideoCosplayUseCase,
    private val savedState: SavedStateHandle,
    private val findCosplayPreviewUseCase: FindCosplayPreviewUseCase,
    private val insertCosplayPreviewUseCase: InsertCosplayPreviewUseCase,
    private val updateCosplayPreviewUseCase: UpdateCosplayPreviewUseCase,
    private val context: Context,
    val player: ExoPlayer
) : ViewModel() {

    private var cosplayPreview: CosplayPreview

    private val _state = MutableStateFlow(FullVideoCosplayState())
    val state = _state.asStateFlow()

    init {
        cosplayPreview = getCosplayPreview()

        viewModelScope.launch(Dispatchers.IO) {
            val videoUrl = getFullVideoCosplayUseCase(cosplayPreview.pageUrl)
            _state.update {
                it.copy(
                    cosplayPreview = cosplayPreview,
                    videoUrl = videoUrl,
                    isLoading = false
                )
            }

            withContext(Dispatchers.Main) {
                val hlsDataSourceFactory = DefaultHttpDataSource.Factory()

                val uri = Uri.Builder()
                    .encodedPath(_state.value.videoUrl)
                    .build()

                val hlsMediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()

                val mediaSource =
                    HlsMediaSource.Factory(hlsDataSourceFactory)
                        .createMediaSource(hlsMediaItem)

                player.addMediaSource(mediaSource)

                player.prepare()
            }
        }

        viewModelScope.launch {
            val preview = findCosplayPreviewUseCase(cosplayPreview.pageUrl)

            cosplayPreview.id = preview?.id ?: 0
            preview?.downloadedAt?.let {
                cosplayPreview.downloadedAt = it
            }

            if (cosplayPreview.id == 0.toLong()) {
                val id = insertCosplayPreviewUseCase(
                    cosplayPreview = cosplayPreview,
                    time = System.currentTimeMillis()
                )
                cosplayPreview.id = id
            } else {
                updateCosplayPreviewUseCase(
                    cosplayPreview = cosplayPreview,
                    time = System.currentTimeMillis()
                )
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun onEvent(event: FullVideoCosplayEvents) {
        when (event) {
            FullVideoCosplayEvents.Download -> {
                downloadVideo()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    private fun downloadVideo() {
        val exportDir =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/Heicos/")

        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val file = File(exportDir, _state.value.cosplayPreview.title + ".mp4")

        val command = String.format(COMMAND, _state.value.videoUrl, file)

        viewModelScope.launch(Dispatchers.IO) {
            cosplayPreview.isDownloaded = true
            val time = System.currentTimeMillis()
            viewModelScope.launch {
                updateCosplayPreviewUseCase(
                    cosplayPreview = cosplayPreview,
                    time = time,
                    isDownloaded = true
                )
            }
            val mpeg = FFmpegKit.execute(command)
            when (mpeg.state) {
                SessionState.FAILED -> {
                    _state.update {
                        it.copy(
                            isDownloadedSuccessful = false,
                            isError = true
                        )
                    }
                }

                SessionState.COMPLETED -> {
                    _state.update {
                        it.copy(
                            isDownloadedSuccessful = true,
                            isError = false
                        )
                    }

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
    }

    private fun getCosplayPreview(): CosplayPreview {
        return savedState.get<CosplayPreview>("cosplayPreview")
            ?: throw IllegalArgumentException("Argument can't be null")
    }

    companion object {
        private const val COMMAND = "-i %s -c copy \"%s\""
    }
}