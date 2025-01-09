package com.heicos.presentation.full_video_cosplay

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.heicos.R
import com.heicos.domain.model.CosplayPreview
import com.heicos.presentation.util.IS_DOWNLOADED
import com.heicos.presentation.util.IS_VIEWED
import com.heicos.presentation.util.LoadingScreen
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Destination
@Composable
fun FullVideoCosplay(
    cosplayPreview: CosplayPreview,
    navController: NavController
) {
    navController.previousBackStackEntry
        ?.savedStateHandle
        ?.set(cosplayPreview.title + IS_VIEWED, true)

    val viewModel: FullVideoCosplayViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (state.isLoading) {
        LoadingScreen()
    } else {
        var expanded by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val localConfiguration = LocalConfiguration.current

        var isFullscreen by rememberSaveable { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            LaunchedEffect(state) {
                if (state.isError || state.isDownloadedSuccessful) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (state.isDownloadedSuccessful) context.getString(R.string.success)
                            else context.getString(R.string.error_message),
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            }

            LaunchedEffect(key1 = isFullscreen) {
                if (isFullscreen)
                    context.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                else
                    context.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            }

            BackHandler(enabled = localConfiguration.orientation == ORIENTATION_LANDSCAPE) {
                isFullscreen = false
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (!isFullscreen) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SelectionContainer(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp),
                        ) {
                            Text(
                                text = cosplayPreview.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { expanded = true }
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                        Box(
                            modifier = Modifier
                                .padding(top = 42.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.open_in_browser)) },
                                    onClick = {
                                        Intent(Intent.ACTION_VIEW).also {
                                            it.data = Uri.parse(cosplayPreview.pageUrl)
                                            context.startActivity(it)
                                        }
                                        expanded = false
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.share)) },
                                    onClick = {
                                        Intent.createChooser(Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, cosplayPreview.pageUrl)
                                            type = "text/plain"
                                        }, null).also { context.startActivity(it) }
                                        expanded = false
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.download)) },
                                    onClick = {
                                        viewModel.onEvent(FullVideoCosplayEvents.Download)
                                        expanded = false
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set(cosplayPreview.title + IS_DOWNLOADED, true)
                                    }
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = if (localConfiguration.orientation == ORIENTATION_LANDSCAPE)
                        Modifier
                            .fillMaxSize()
                            .weight(1f)
                    else Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { context ->
                            PlayerView(context).apply {
                                player = viewModel.player

                                setFullscreenButtonClickListener {
                                    isFullscreen = !isFullscreen
                                }

                                setShowNextButton(false)
                                setShowPreviousButton(false)
                                setShowFastForwardButton(false)
                                setShowRewindButton(false)
                                setShowShuffleButton(false)
                                setShowSubtitleButton(false)

                                if (Build.VERSION.SDK_INT >= 29) {
                                    transitionAlpha = 0.9f
                                }
                            }
                        },
                        update = {
                            when (lifecycle) {
                                Lifecycle.Event.ON_PAUSE -> {
                                    it.onPause()
                                    it.player?.pause()
                                }

                                Lifecycle.Event.ON_RESUME -> {
                                    it.onResume()
                                }

                                else -> Unit
                            }
                        }
                    )
                }
            }
        }


    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.setScreenOrientation(orientation: Int) {
    val activity = this.findActivity() ?: return
    activity.requestedOrientation = orientation
    if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        hideSystemUi()
    } else {
        showSystemUi()
    }
}

fun Context.hideSystemUi() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}

fun Context.showSystemUi() {
    val activity = this.findActivity() ?: return
    val window = activity.window ?: return
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
}
