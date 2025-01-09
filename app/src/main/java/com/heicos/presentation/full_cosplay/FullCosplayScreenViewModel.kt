package com.heicos.presentation.full_cosplay

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.BuildConfig
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.FindCosplayPreviewUseCase
import com.heicos.domain.use_case.GetCosplayTagsUseCase
import com.heicos.domain.use_case.GetFullCosplayUseCase
import com.heicos.domain.use_case.InsertCosplayPreviewUseCase
import com.heicos.domain.use_case.UpdateCosplayPreviewUseCase
import com.heicos.utils.Resource
import com.heicos.utils.manager.CosplayDownloader
import com.heicos.utils.time.convertTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullCosplayScreenViewModel @Inject constructor(
    private val getFullCosplayUseCase: GetFullCosplayUseCase,
    private val getCosplayTagsUseCase: GetCosplayTagsUseCase,
    private val findCosplayPreviewUseCase: FindCosplayPreviewUseCase,
    private val insertCosplayPreviewUseCase: InsertCosplayPreviewUseCase,
    private val updateCosplayPreviewUseCase: UpdateCosplayPreviewUseCase,
    private val cosplayDownloader: CosplayDownloader,
    private val savedState: SavedStateHandle
) : ViewModel() {

    private var cosplayPreview: CosplayPreview

    private val _state = MutableStateFlow(FullCosplayScreenState())
    val state = _state.asStateFlow()

    val gridState = LazyGridState()

    init {
        cosplayPreview = getCosplayPreview()

        _state.update {
            it.copy(downloadTime = cosplayPreview.downloadTime)
        }

        if (cosplayPreview.storyPageUrl.isEmpty()) {
            val cosplayName = getCosplayName()
            cosplayPreview =
                CosplayPreview(
                    title = cosplayName,
                    pageUrl = "${BuildConfig.baseUrl}/image/${cosplayName}/",
                    storyPageUrl = "/story/${cosplayName}/"
                )
            _state.value = _state.value.copy(
                title = cosplayName
            )
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

        loadCosplays()
    }

    private fun loadCosplays() {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteData = getFullCosplayUseCase(cosplayPreview.storyPageUrl)

            remoteData.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.value = _state.value.copy(message = result.message)
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }

                    is Resource.Success -> {
                        result.data?.let { data ->
                            if (data.isNotEmpty()) {
                                _state.value = _state.value.copy(
                                    cosplaysPhotoUrl = data,
                                    message = null
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    fun onEvent(event: FullCosplayScreenEvents) {
        when (event) {
            FullCosplayScreenEvents.DownloadAllImages -> {
                cosplayPreview.isDownloaded = true
                downloadAllImages()
                val time = System.currentTimeMillis()
                viewModelScope.launch {
                    updateCosplayPreviewUseCase(
                        cosplayPreview = cosplayPreview,
                        time = time,
                        isDownloaded = true
                    )
                }
                addTime(time)
            }

            FullCosplayScreenEvents.LoadCosplayTags -> {
                getCosplayTags()
            }

            is FullCosplayScreenEvents.DownloadImage -> {
                cosplayPreview.isDownloaded = true
                downloadImage(event.url)
                val time = System.currentTimeMillis()
                viewModelScope.launch {
                    updateCosplayPreviewUseCase(
                        cosplayPreview = cosplayPreview,
                        time = time,
                        isDownloaded = true
                    )
                }
                addTime(time)
            }

            is FullCosplayScreenEvents.ScrollToItem -> {
                scrollToItem(event.index)
            }
        }
    }

    private fun downloadImage(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cosplayDownloader.downloadFile(url, cosplayPreview.title)
        }
    }

    private fun downloadAllImages() {
        viewModelScope.launch(Dispatchers.IO) {
            val cosplayTitle = cosplayPreview.title
            cosplayDownloader.downloadFiles(_state.value.cosplaysPhotoUrl, cosplayTitle)
        }
    }

    private fun getCosplayTags() {
        viewModelScope.launch(Dispatchers.IO) {
            val tags = getCosplayTagsUseCase(cosplayPreview.pageUrl)
            tags.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.value = _state.value.copy(messageInMoreInfo = result.message)
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            tagsIsLoading = result.isLoading
                        )
                    }

                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            tagsIsLoading = false,
                            cosplayTags = result.data ?: emptyList(),
                        )
                    }
                }
            }
        }
    }

    private fun scrollToItem(index: Int) {
        viewModelScope.launch { gridState.scrollToItem(index) }
    }

    private fun getCosplayPreview(): CosplayPreview {
        return savedState.get<CosplayPreview>("cosplayPreview")
            ?: throw IllegalArgumentException("Argument can't be null")
    }

    private fun getCosplayName(): String {
        return savedState.get<String>("cosplayName")
            ?: throw IllegalArgumentException("Argument can't be null")
    }

    private fun addTime(time: Long) {
        _state.value = _state.value.copy(
            downloadTime = convertTime(time)
        )
    }

}