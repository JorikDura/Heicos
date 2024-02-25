package com.heicos.presentation.cosplays.ranking_cosplays

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heicos.R
import com.heicos.presentation.cosplays.CosplayScreenItem
import com.heicos.presentation.destinations.FullCosplayScreenDestination
import com.heicos.presentation.util.ErrorMessage
import com.heicos.presentation.util.LoadingScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun RankingCosplaysScreen(
    viewModel: RankingCosplaysViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val gridCells = 2
    val state by viewModel.state.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    if(pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.onEvent(RankingCosplaysEvents.Refresh)
            pullRefreshState.endRefresh()
        }
    }
    Box(
        modifier = Modifier
            .nestedScroll(pullRefreshState.nestedScrollConnection)
            .fillMaxSize(),
    ) {
        if (!state.message.isNullOrEmpty()) {
            ErrorMessage(message = state.message!!)
        } else {
            if (state.isLoading) {
                LoadingScreen()
            } else {
                if (state.isEmpty) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = R.string.nothing_found))
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridCells),
                    state = viewModel.gridState
                ) {
                    items(state.cosplays) { cosplay ->
                        CosplayScreenItem(
                            cosplay = cosplay,
                            onItemClickListener = {
                                navigator.navigate(
                                    FullCosplayScreenDestination(
                                        cosplayPreview = cosplay
                                    )
                                )
                            }
                        )
                    }
                    item(
                        span = { GridItemSpan(gridCells) }
                    ) {
                        if (state.nextDataIsLoading) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 36.dp, bottom = 36.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            if (!state.nextDataIsEmpty) {
                                SideEffect {
                                    viewModel.onEvent(RankingCosplaysEvents.LoadNextData)
                                }
                            }
                        }
                    }
                }
            }
        }
        val scaleFraction = if (pullRefreshState.isRefreshing) 1f else
            LinearOutSlowInEasing.transform(pullRefreshState.progress).coerceIn(0f, 1f)
        PullToRefreshContainer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
            state = pullRefreshState,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }

}