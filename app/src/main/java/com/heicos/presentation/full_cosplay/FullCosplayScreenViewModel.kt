package com.heicos.presentation.full_cosplay

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.GetCosplayTagsUseCase
import com.heicos.domain.use_case.GetFullCosplayUseCase
import com.heicos.utils.Resource
import com.heicos.utils.manager.CosplayDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullCosplayScreenViewModel @Inject constructor(
    private val getFullCosplayUseCase: GetFullCosplayUseCase,
    private val getCosplayTagsUseCase: GetCosplayTagsUseCase,
    private val cosplayDownloader: CosplayDownloader,
    private val savedState: SavedStateHandle
) : ViewModel() {

    private var cosplayPreview: CosplayPreview

    private val _state = MutableStateFlow(FullCosplayScreenState())
    val state get() = _state

    val gridState = LazyGridState()

    init {
        cosplayPreview = getCosplayPreview()
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
                downloadAllImages()
            }

            FullCosplayScreenEvents.LoadCosplayTags -> {
                getCosplayTags()
            }

            is FullCosplayScreenEvents.DownloadImage -> {
                downloadImage(event.url)
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
            _state.value.cosplaysPhotoUrl.forEach { imageUrl ->
                cosplayDownloader.downloadFile(imageUrl, cosplayTitle)
            }
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

}