package com.heicos.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.heicos.R
import com.heicos.presentation.NavGraphs
import com.heicos.presentation.connection.ConnectionScreen
import com.heicos.presentation.util.rememberAnimations
import com.heicos.ui.theme.HeicosTheme
import com.heicos.utils.manager.ConnectivityObserver
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeicosTheme {
                val systemUIController = rememberSystemUiController()
                val navigationBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                val statusBarColor = MaterialTheme.colorScheme.surface
                val isDarkTheme = isSystemInDarkTheme()

                SideEffect {
                    systemUIController.setNavigationBarColor(
                        color = navigationBarColor,
                        navigationBarContrastEnforced = false,
                    )
                    systemUIController.setStatusBarColor(
                        color = statusBarColor,
                        darkIcons = !isDarkTheme
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainScreenViewModel = hiltViewModel()
                    val connectionStatus =
                        viewModel.state.collectAsState(
                            initial = if (viewModel.currentNetwork != null) ConnectivityObserver.Status.Available
                            else ConnectivityObserver.Status.Unavailable
                        )
                    when (connectionStatus.value) {
                        ConnectivityObserver.Status.Available -> {
                            DestinationsNavHost(
                                navGraph = NavGraphs.root,
                                engine = rememberAnimations()
                            )
                        }

                        ConnectivityObserver.Status.Unavailable -> {
                            ConnectionScreen(message = stringResource(id = R.string.connection_unavailable))
                        }

                        ConnectivityObserver.Status.Losing -> {
                            ConnectionScreen(message = stringResource(id = R.string.connection_losing))
                        }

                        ConnectivityObserver.Status.Lost -> {
                            ConnectionScreen(message = stringResource(id = R.string.connection_lost))
                        }
                    }
                }
            }
        }
    }
}
