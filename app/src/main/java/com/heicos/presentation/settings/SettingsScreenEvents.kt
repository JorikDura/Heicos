package com.heicos.presentation.settings

import com.heicos.presentation.settings.types.BackupTypes

sealed class SettingsScreenEvents {
    data class Backup(val type: BackupTypes) : SettingsScreenEvents()
    data class Restore(val type: BackupTypes) : SettingsScreenEvents()
    data class Truncate(val type: BackupTypes) : SettingsScreenEvents()
    data object ResetStatus : SettingsScreenEvents()
}