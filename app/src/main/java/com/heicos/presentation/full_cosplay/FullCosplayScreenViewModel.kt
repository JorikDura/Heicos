package com.heicos.presentation.full_cosplay

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.GetFullCosplayUseCase
import com.heicos.utils.Resource
import com.heicos.utils.manager.CosplayDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullCosplayScreenViewModel @Inject constructor(
    private val getFullCosplayUseCase: GetFullCosplayUseCase,
    private val cosplayDownloader: CosplayDownloader,
    private val savedState: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(FullCosplayScreenState(isLoading = true))

    val gridState = LazyGridState()

    init {
        loadCosplays()
    }

    private fun loadCosplays() {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteData = getFullCosplayUseCase(getArgument())

            remoteData.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        state = state.copy(message = result.message)
                    }

                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                        result.data?.let { data ->
                            if (data.isNotEmpty()) {
                                state = state.copy(
                                    isLoading = false,
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
            cosplayDownloader.downloadFile(url)
        }
    }

    private fun downloadAllImages() {
        viewModelScope.launch(Dispatchers.IO) {
            state.cosplaysPhotoUrl.forEach { imageUrl ->
                cosplayDownloader.downloadFile(imageUrl)
            }
        }
    }

    private fun scrollToItem(index: Int) {
        viewModelScope.launch { gridState.scrollToItem(index) }
    }

    private fun getArgument(): String {
        return savedState.get<CosplayPreview>("cosplayPreview")?.storyPageUrl
            ?: throw IllegalArgumentException("Argument can't be null")
    }
}