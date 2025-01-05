package com.heicos.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.data.database.entities.SearchQueryEntity
import com.heicos.domain.repository.BackupRepository
import com.heicos.presentation.settings.types.BackupTypes
import com.heicos.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    fun onEvent(event: SettingsScreenEvents) {
        when (event) {
            is SettingsScreenEvents.Backup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    backupRepository.export(getTableName(event.type)).collect { status ->
                        updateState(status)
                    }
                }

            }

            is SettingsScreenEvents.Restore -> {
                viewModelScope.launch(Dispatchers.IO) {
                    backupRepository.import(getTableName(event.type)).collect { status ->
                        updateState(status)
                    }
                }
            }

            SettingsScreenEvents.ResetStatus -> {
                _state.value = _state.value.copy(
                    isSuccess = false,
                    isError = false,
                    errorMessage = ""
                )
            }

            is SettingsScreenEvents.Truncate -> {
                viewModelScope.launch(Dispatchers.IO) {
                    backupRepository.truncate(getTableName(event.type)).collect { status ->
                        updateState(status)
                    }
                }
            }
        }
    }

    private fun getTableName(type: BackupTypes): String {
        return when (type) {
            BackupTypes.Search -> {
                SearchQueryEntity::class.simpleName!!
            }

            BackupTypes.ViewAndDownload -> {
                CosplayPreviewEntity::class.simpleName!!
            }
        }
    }

    private fun updateState(status: Resource<String>) {
        when (status) {
            is Resource.Error -> {
                status.message?.let { message ->
                    _state.value = _state.value.copy(
                        isError = true,
                        errorMessage = message
                    )
                }
            }

            else -> {
                _state.value = _state.value.copy(
                    isSuccess = true
                )
            }
        }
    }

}