package com.heicos.presentation.cosplays

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heicos.R
import com.heicos.presentation.destinations.FullCosplayScreenDestination
import com.heicos.presentation.util.LoadingScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun CosplaysScreen(
    viewModel: CosplaysScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val gridCells = 2
    val state = viewModel.screenState

    var query by remember {
        mutableStateOf(viewModel.searchQuery)
    }
    var searchBarStatus by remember {
        mutableStateOf(false)
    }
    val historySearch = remember {
        mutableStateListOf<String>()
    }

    Box(
        modifier = Modifier
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
                viewModel.onEvent(CosplaysScreenEvents.Search(query))
                searchBarStatus = false
                if (!historySearch.contains(query)) {
                    historySearch.add(query)
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
                IconButton(
                    onClick = {
                        if (query.isNotEmpty()) {
                            query = ""
                            searchBarStatus = false
                            viewModel.onEvent(CosplaysScreenEvents.Reset)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = null
                    )
                }
            }
        ) {
            historySearch.reversed().forEach { historyItem ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { query = historyItem },
                    headlineContent = { Text(text = historyItem) },
                    leadingContent = {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    }
                )
            }
        }

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
                columns = GridCells.Fixed(gridCells)
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
                                viewModel.onEvent(CosplaysScreenEvents.LoadNextData)
                            }
                        }
                    }
                }
            }
        }
    }
}
