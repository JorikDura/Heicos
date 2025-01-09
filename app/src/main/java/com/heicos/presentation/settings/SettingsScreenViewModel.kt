package com.heicos.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.data.database.entities.SearchQueryEntity
import com.heicos.domain.repository.BackupRepository
import com.heicos.presentation.settings.types.BackupTypes
import com.heicos.presentation.util.IS_NOTIFICATION_ENABLED
import com.heicos.presentation.util.SETTINGS
import com.heicos.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    init {
        val isNotificationsEnabled = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE)
            .getBoolean(IS_NOTIFICATION_ENABLED, false)
        _state.update {
            it.copy(isNotificationEnabled = isNotificationsEnabled)
        }
    }

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

            is SettingsScreenEvents.ChangeNotificationSetting -> {
                context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit().apply {
                    putBoolean(IS_NOTIFICATION_ENABLED, event.isEnabled)
                    commit()
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
                    _state.update {
                        it.copy(
                            isError = true,
                            errorMessage = message
                        )
                    }
                }
            }

            else -> {
                _state.update {
                    it.copy(isSuccess = true)
                }
            }
        }
    }

}