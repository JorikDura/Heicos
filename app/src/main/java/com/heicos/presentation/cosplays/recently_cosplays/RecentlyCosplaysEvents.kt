package com.heicos.presentation.cosplays.recently_cosplays

sealed class RecentlyCosplaysEvents {
    data object Refresh : RecentlyCosplaysEvents()
    data object LoadNextData : RecentlyCosplaysEvents()
}