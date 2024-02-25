package com.heicos.presentation.cosplays

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heicos.R
import com.heicos.presentation.cosplays.types.CosplayTypes
import com.heicos.presentation.destinations.FullCosplayScreenDestination
import com.heicos.presentation.util.ErrorMessage
import com.heicos.presentation.util.LoadingScreen
import com.heicos.presentation.util.SwipeToDeleteContainer
import com.heicos.presentation.util.getActivity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun CosplaysScreen(
    viewModel: CosplaysScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<FullCosplayScreenDestination, String>
) {
    var query by remember {
        mutableStateOf(viewModel.searchQuery)
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit

            is NavResult.Value -> {
                query = result.value
                viewModel.onEvent(CosplaysScreenEvents.Search(query))
            }
        }
    }
    val scope = rememberCoroutineScope()
    val gridCells = 2
    val state by viewModel.state.collectAsState()

    var searchBarStatus by remember {
        mutableStateOf(false)
    }

    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.onEvent(CosplaysScreenEvents.Refresh)
            pullRefreshState.endRefresh()
        }
    }

    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(12.dp))
                CosplayTypes.entries.forEach { cosplay ->
                    NavigationDrawerItem(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        icon = { Icon(imageVector = cosplay.icon, contentDescription = null) },
                        label = { Text(text = stringResource(id = cosplay.title)) },
                        selected = cosplay.cosplayType == state.currentCosplayType,
                        onClick = {
                            if (state.currentCosplayType != cosplay.cosplayType) {
                                scope.launch { drawerState.close() }
                                query = ""
                                viewModel.onEvent(CosplaysScreenEvents.ChangeCosplayType(cosplay.cosplayType))
                            }
                        },
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }
                .nestedScroll(pullRefreshState.nestedScrollConnection),
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
                    viewModel.onEvent(CosplaysScreenEvents.Search(query))
                    searchBarStatus = false

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
                                viewModel.onEvent(CosplaysScreenEvents.Reset)
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
                LaunchedEffect(Unit) {
                    viewModel.onEvent(CosplaysScreenEvents.LoadSearchQueries)
                }
                if (state.isHistoryLoading) {
                    LoadingScreen()
                } else {
                    LazyColumn {
                        items(
                            items = state.history.reversed(),
                            key = { it.id }
                        ) { historyItem ->
                            SwipeToDeleteContainer(
                                item = historyItem,
                                onDelete = {
                                    viewModel.onEvent(
                                        CosplaysScreenEvents.DeleteSearchItem(
                                            historyItem
                                        )
                                    )
                                }
                            ) { item ->
                                ListItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { query = item.query },
                                    headlineContent = { Text(text = historyItem.query) },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Filled.Refresh,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                        if (state.history.isNotEmpty()) {
                            item {
                                ListItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.onEvent(CosplaysScreenEvents.DeleteHistoryQuery) },
                                    headlineContent = { Text(text = stringResource(id = R.string.clean)) },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

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
                            when {
                                !state.nextDataMessage.isNullOrEmpty() -> {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 36.dp, bottom = 36.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = state.nextDataMessage ?: "")
                                    }
                                }

                                state.nextDataIsLoading -> {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 36.dp, bottom = 36.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                else -> {
                                    if (!state.nextDataIsEmpty) {
                                        SideEffect {
                                            viewModel.onEvent(CosplaysScreenEvents.LoadNextData)
                                        }
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
                    .padding(top = 72.dp)
                    .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
                state = pullRefreshState,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
            return@BackHandler
        }
        if (searchBarStatus) {
            searchBarStatus = false
            return@BackHandler
        }
        if (query.isNotEmpty()) {
            query = ""
            viewModel.onEvent(CosplaysScreenEvents.Reset)
            return@BackHandler
        }
        context.getActivity()?.finish()
    }
}