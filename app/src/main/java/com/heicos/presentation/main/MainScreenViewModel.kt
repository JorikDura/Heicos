package com.heicos.presentation.main

import androidx.lifecycle.ViewModel
import com.heicos.utils.manager.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    networkObserver: ConnectivityObserver
) : ViewModel() {

    val state = networkObserver.observe()
    val currentNetwork = networkObserver.getCurrentNetwork()
}