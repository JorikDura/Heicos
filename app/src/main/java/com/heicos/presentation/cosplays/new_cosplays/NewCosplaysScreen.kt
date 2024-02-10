package com.heicos.presentation.cosplays.new_cosplays

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heicos.R
import com.heicos.presentation.cosplays.CosplayScreenItem
import com.heicos.presentation.destinations.FullCosplayScreenDestination
import com.heicos.presentation.util.ErrorMessage
import com.heicos.presentation.util.LoadingScreen
import com.heicos.presentation.util.SwipeToDeleteContainer
import com.heicos.presentation.util.getActivity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun NewCosplaysScreen(
    viewModel: NewCosplaysScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val gridCells = 2
    val state = viewModel.state

    var query by remember {
        mutableStateOf(viewModel.searchQuery)
    }
    var searchBarStatus by remember {
        mutableStateOf(false)
    }
    val pullRefreshState = rememberPullRefreshState(
        state.isRefreshing, { viewModel.onEvent(NewCosplaysScreenEvents.Refresh) })

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxWidth()
            .semantics { isTraversalGroup = true },
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = -1f },
            query = query,
            onQueryChange = {
                query = it
            },
            onSearch = {
                viewModel.onEvent(NewCosplaysScreenEvents.Search(query))
                searchBarStatus = false
                val queryContains = state.history.find { searchQuery ->
                    searchQuery.query == query
                }
                if (queryContains == null) {
                    viewModel.onEvent(NewCosplaysScreenEvents.AddHistoryQuery(query))
                }
            },
            active = searchBarStatus,
            onActiveChange = {
                searchBarStatus = it
            },
            placeholder = {
                Text(text = stringResource(id = R.string.search))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300))
                ) {
                    IconButton(
                        onClick = {
                            query = ""
                            viewModel.onEvent(NewCosplaysScreenEvents.Reset)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = null
                        )
                    }
                }
            }
        ) {
            LazyColumn {
                items(
                    items = state.history.reversed(),
                    key = { it.id }
                ) { historyItem ->
                    SwipeToDeleteContainer(
                        item = historyItem,
                        onDelete = {
                            viewModel.onEvent(NewCosplaysScreenEvents.DeleteSearchItem(historyItem))
                        }
                    ) { item ->
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { query = item.query },
                            headlineContent = { Text(text = historyItem.query) },
                            leadingContent = {
                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                            }
                        )
                    }

                }
                if (state.history.isNotEmpty()) {
                    item {
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onEvent(NewCosplaysScreenEvents.DeleteHistoryQuery) },
                            headlineContent = { Text(text = stringResource(id = R.string.clean)) },
                            leadingContent = {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }

        if (!state.message.isNullOrEmpty()) {
            ErrorMessage(message = state.message)
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
                    contentPadding = PaddingValues(top = 72.dp),
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
                                    viewModel.onEvent(NewCosplaysScreenEvents.LoadNextData)
                                }
                            }
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            modifier = Modifier
                .padding(top = 72.dp)
                .align(Alignment.TopCenter),
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.background
        )
    }
    BackHandler {
        if (searchBarStatus) {
            searchBarStatus = false
            return@BackHandler
        }
        if (query.isNotEmpty()) {
            query = ""
            viewModel.onEvent(NewCosplaysScreenEvents.Reset)
            return@BackHandler
        }
        context.getActivity()?.finish()
    }
}