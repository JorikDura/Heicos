package com.heicos.presentation.full_video_cosplay

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
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
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.GetFullVideoCosplayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.String
import javax.inject.Inject


@HiltViewModel
class FullVideoCosplayViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val getFullVideoCosplayUseCase: GetFullVideoCosplayUseCase,
    private val savedState: SavedStateHandle,
    private val context: Context,
    val player: ExoPlayer
) : ViewModel() {

    private val _state = MutableStateFlow(FullVideoCosplayState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val cosplayPreview = getCosplayPreview()

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
    }

    @OptIn(UnstableApi::class)
    fun onEvent(event: FullVideoCosplayEvents) {
        when (event) {
            FullVideoCosplayEvents.Download -> {
                val exportDir =
                    File(getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/Heicos/")

                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }

                val file = File(exportDir, _state.value.cosplayPreview.title + ".mp4")

                val cmd = String.format(
                    "-i %s -c copy \"%s\"",
                    _state.value.videoUrl,
                    file
                )

                viewModelScope.launch(Dispatchers.IO) {
                    FFmpegKit.execute(cmd)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    private fun getCosplayPreview(): CosplayPreview {
        return savedState.get<CosplayPreview>("cosplayPreview")
            ?: throw IllegalArgumentException("Argument can't be null")
    }
}