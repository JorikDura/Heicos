package com.heicos.presentation.cosplays

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.use_case.GetCosplaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CosplaysScreenViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase
) : ViewModel() {

    private var page = 1
    private var cosplaysCache = mutableListOf<CosplayPreview>()

    var screenState by mutableStateOf(CosplaysScreenState(isLoading = true))

    init {
        loadNextData()
    }

    fun loadNextData(loadingNextData: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loadingNextData) {
                screenState = screenState.copy(
                    isLoading = false,
                    nextDataIsLoading = true,
                    cosplays = cosplaysCache
                )
            }

            val result = getCosplaysUseCase(page)

            if (result.isNotEmpty()) {
                cosplaysCache.addAll(result)
                screenState = screenState.copy(
                    isLoading = false,
                    nextDataIsLoading = false,
                    cosplays = cosplaysCache
                )
            }

            page++
        }
    }
}