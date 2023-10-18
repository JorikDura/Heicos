package com.heicos.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.heicos.ui.theme.HeicosTheme

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
                    Text(text = "Test")
                }
            }
        }
    }
}
