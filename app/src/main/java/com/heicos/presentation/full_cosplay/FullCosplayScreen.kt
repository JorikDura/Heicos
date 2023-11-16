package com.heicos.presentation.full_cosplay

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Dimension
import com.heicos.R
import com.heicos.domain.model.CosplayPreview
import com.heicos.presentation.util.LoadingScreen
import com.heicos.presentation.util.USER_AGENT_MOZILLA
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun FullCosplayScreen(
    cosplayPreview: CosplayPreview,
    viewModel: FullCosplayScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state = viewModel.screenState

    if (state.isLoading) {
        LoadingScreen()
    } else {
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
        var isPagerMode by remember { mutableStateOf(false) }
        val pagerState = rememberPagerState {
            state.cosplaysPhotoUrl.size
        }
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
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
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.download_all)) },
                            onClick = {
                                viewModel.onEvent(FullCosplayScreenEvents.DownloadAllImages)
                                expanded = false
                            }
                        )
                        if (isPagerMode) {
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.download)) },
                                onClick = {
                                    viewModel.onEvent(
                                        FullCosplayScreenEvents.DownloadImage(
                                            state.cosplaysPhotoUrl[pagerState.currentPage]
                                        )
                                    )
                                    expanded = false
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.to_last_picture)) },
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(state.cosplaysPhotoUrl.size)
                                        expanded = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
            AnimatedContent(
                modifier = Modifier
                    .weight(1f),
                targetState = isPagerMode,
                label = "sliderMode"
            ) { isPager ->
                if (isPager) {
                    HorizontalPager(
                        state = pagerState
                    ) { index ->
                        CosplayImageItem(data = state.cosplaysPhotoUrl[index])
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = viewModel.gridState
                    ) {
                        items(state.cosplaysPhotoUrl) { cosplayUrl ->
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .height(250.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(7.dp))
                                    .clickable {
                                        val cosplayIndex =
                                            state.cosplaysPhotoUrl.indexOf(cosplayUrl)
                                        scope.launch { pagerState.scrollToPage(cosplayIndex) }
                                        isPagerMode = !isPagerMode
                                    }
                            ) {
                                CosplayImageItem(data = cosplayUrl)
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = cosplayPreview.date)
                AnimatedContent(
                    targetState = isPagerMode,
                    label = "cosplaysListSize",
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { isPager ->
                    if (isPager) {
                        Text(text = "${pagerState.currentPage + 1}/${state.cosplaysPhotoUrl.size}")
                    } else {
                        Text(text = "${stringResource(id = R.string.cosplays_list_size_message)} ${state.cosplaysPhotoUrl.size}")
                    }
                }
            }
        }

        //custom back handler — when screen in pager mode, then just off that
        BackHandler {
            if (isPagerMode) {
                viewModel.onEvent(FullCosplayScreenEvents.ScrollToItem(pagerState.currentPage))
                isPagerMode = !isPagerMode
            } else {
                navigator.popBackStack()
            }
        }
    }
}

@Composable
fun CosplayImageItem(
    data: String,
    scale: ContentScale = ContentScale.None
) {
    SubcomposeAsyncImage(
        modifier = Modifier
            .fillMaxSize(),
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .addHeader("User-Agent", USER_AGENT_MOZILLA)
            .crossfade(true)
            .size(Dimension.Undefined, Dimension.Pixels(1920))
            .build(),
        contentDescription = null,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.error_message))
            }
        },
        contentScale = scale
    )
}