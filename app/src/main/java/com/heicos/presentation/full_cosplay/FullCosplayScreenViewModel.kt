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

    var screenState by mutableStateOf(FullCosplayScreenState(isLoading = true))

    val gridState = LazyGridState()

    init {
        loadCosplays()
    }

    private fun loadCosplays() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getFullCosplayUseCase(getArgument())

            if (result.isNotEmpty()) {
                screenState = screenState.copy(
                    isLoading = false,
                    cosplaysPhotoUrl = result
                )
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
        }
    }

    private fun downloadImage(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cosplayDownloader.downloadFile(url)
        }
    }

    private fun downloadAllImages() {
        viewModelScope.launch(Dispatchers.IO) {
            screenState.cosplaysPhotoUrl.forEach { imageUrl ->
                cosplayDownloader.downloadFile(imageUrl)
            }
        }
    }

    private fun getArgument(): String {
        return savedState.get<CosplayPreview>("cosplayPreview")?.storyPageUrl
            ?: throw IllegalArgumentException("Argument can't be null")
    }
}