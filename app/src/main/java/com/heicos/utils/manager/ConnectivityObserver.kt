package com.heicos.utils.manager

import android.net.Network
import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>
    fun getCurrentNetwork(): Network?
    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}