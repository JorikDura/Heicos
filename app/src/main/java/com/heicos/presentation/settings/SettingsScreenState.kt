package com.heicos.presentation.settings

data class SettingsScreenState(
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isNotificationEnabled: Boolean = false
)
