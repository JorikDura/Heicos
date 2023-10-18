package com.heicos.presentation.full_cosplay

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.heicos.R
import com.heicos.domain.model.CosplayPreview
import com.heicos.presentation.util.USER_AGENT_MOZILLA
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun FullCosplayScreen(
    cosplayPreview: CosplayPreview,
    viewModel: FullCosplayScreenViewModel = hiltViewModel()
) {
    val state = viewModel.screenState

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val context = LocalContext.current
        var expanded by remember { mutableStateOf(false) }
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
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = cosplayPreview.title
                )
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
                            text = { Text(text = stringResource(id = R.string.download)) },
                            onClick = {
                                viewModel.downloadImage(state.cosplaysPhotoUrl[pagerState.currentPage])
                                expanded = false
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.download_all)) },
                            onClick = {
                                viewModel.downloadAllImages()
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
            HorizontalPager(
                modifier = Modifier
                    .weight(1f),
                state = pagerState
            ) { index ->
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = ImageRequest.Builder(context)
                        .data(state.cosplaysPhotoUrl[index])
                        .addHeader("User-Agent", USER_AGENT_MOZILLA)
                        .crossfade(true)
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
                    }
                )
            }
            Row(
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = cosplayPreview.date)
                Text(text = "${pagerState.currentPage + 1}/${state.cosplaysPhotoUrl.size}")
            }
        }
    }
}