package com.heicos.presentation.full_cosplay

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Dimension
import com.heicos.R
import com.heicos.domain.model.CosplayPreview
import com.heicos.presentation.util.ErrorMessage
import com.heicos.presentation.util.LoadingScreen
import com.heicos.presentation.util.USER_AGENT_MOZILLA
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Destination
@Composable
fun FullCosplayScreen(
    cosplayPreview: CosplayPreview,
    viewModel: FullCosplayScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()

    if (!state.message.isNullOrEmpty()) {
        ErrorMessage(message = state.message!!)
    } else {
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
            var openBottomSheet by rememberSaveable { mutableStateOf(false) }
            val edgeToEdgeEnabled by remember { mutableStateOf(false) }
            val skipPartiallyExpanded by remember { mutableStateOf(false) }
            val bottomSheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = skipPartiallyExpanded
            )
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
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.download_all)) },
                                onClick = {
                                    viewModel.onEvent(FullCosplayScreenEvents.DownloadAllImages)
                                    expanded = false
                                }
                            )
                            if (isPagerMode) {
                                HorizontalDivider()
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
                                HorizontalDivider()
                                val currentPage = pagerState.currentPage + 1
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(
                                                id = if (currentPage == state.cosplaysPhotoUrl.size)
                                                    R.string.to_first_picture
                                                else R.string.to_last_picture
                                            )
                                        )
                                    },
                                    onClick = {
                                        scope.launch {
                                            if (currentPage == state.cosplaysPhotoUrl.size)
                                                pagerState.animateScrollToPage(0)
                                            else pagerState.animateScrollToPage(state.cosplaysPhotoUrl.size)
                                            expanded = false
                                        }
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.more_info)) },
                                onClick = {
                                    openBottomSheet = !openBottomSheet
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f),
                ) {
                    AnimatedContent(
                        modifier = Modifier
                            .fillMaxSize(),
                        targetState = isPagerMode,
                        label = "sliderMode"
                    ) { isPager ->
                        if (isPager) {
                            HorizontalPager(
                                modifier = Modifier
                                    .clipToBounds(),
                                state = pagerState,
                                beyondBoundsPageCount = 1
                            ) { index ->
                                CosplayImageItem(
                                    data = state.cosplaysPhotoUrl[index],
                                    isZoomable = true,
                                    isActivePage = pagerState.settledPage == index,
                                    scale = ContentScale.FillWidth
                                )
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
                                        CosplayImageItem(
                                            data = cosplayUrl,
                                            scale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp)
                    ) {
                        AnimatedContent(
                            targetState = isPagerMode,
                            label = "cosplaysListSize",
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { isPager ->
                            if (isPager) {
                                Text(text = "${pagerState.currentPage + 1}/${state.cosplaysPhotoUrl.size}")
                            }
                        }
                    }
                }
            }

            if (openBottomSheet) {
                val windowInsets = if (edgeToEdgeEnabled)
                    WindowInsets(0) else BottomSheetDefaults.windowInsets

                ModalBottomSheet(
                    onDismissRequest = { openBottomSheet = false },
                    sheetState = bottomSheetState,
                    windowInsets = windowInsets
                ) {
                    LaunchedEffect(Unit) {
                        viewModel.onEvent(FullCosplayScreenEvents.LoadCosplayTags)
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 12.dp, top = 24.dp, end = 12.dp, bottom = 24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            fontSize = 18.sp,
                            text = stringResource(id = R.string.title, cosplayPreview.title)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            fontSize = 18.sp,
                            text = stringResource(id = R.string.date, cosplayPreview.date)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            fontSize = 18.sp,
                            text = stringResource(id = R.string.total, state.cosplaysPhotoUrl.size)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        when {
                            state.tagsIsLoading -> {
                                Box(
                                    modifier = Modifier

                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            !state.messageInMoreInfo.isNullOrEmpty() -> {
                                Text(
                                    fontSize = 18.sp,
                                    text = state.messageInMoreInfo!!
                                )
                            }

                            else -> {
                                val tagsIsNotEmpty = state.cosplayTags.isNotEmpty()
                                Text(
                                    fontSize = 18.sp,
                                    text = stringResource(
                                        id = R.string.tags, if (tagsIsNotEmpty) ""
                                        else stringResource(id = R.string.empty)
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                AnimatedVisibility(visible = tagsIsNotEmpty) {
                                    FlowRow(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        state.cosplayTags.forEach { tag ->
                                            TagContainer(
                                                text = tag,
                                                onClickListener = {

                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
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
}

@Composable
fun CosplayImageItem(
    data: String,
    scale: ContentScale = ContentScale.None,
    isZoomable: Boolean = false,
    isActivePage: Boolean = false
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .addHeader("User-Agent", USER_AGENT_MOZILLA)
            .crossfade(true)
            .size(Dimension.Undefined, Dimension.Pixels(1920))
            .build(),
        imageLoader = imageLoader
    )

    when (imagePainter.state) {
        AsyncImagePainter.State.Empty -> {

        }

        is AsyncImagePainter.State.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.error_message))
            }
        }

        is AsyncImagePainter.State.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AsyncImagePainter.State.Success -> {
            if (isZoomable) {
                val zoomState = rememberZoomState(
                    maxScale = 2.25f,
                    contentSize = imagePainter.intrinsicSize
                )

                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(
                            zoomState = zoomState,
                            onDoubleTap = { position ->
                                val targetScale = when {
                                    zoomState.scale < 1.75f -> 1.75f
                                    zoomState.scale < 2.25f -> 2.25f
                                    zoomState.scale == 2.25f -> 1.0f
                                    else -> 1.0f
                                }
                                zoomState.changeScale(targetScale, position)
                            }
                        ),
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = scale
                )

                LaunchedEffect(!isActivePage) {
                    zoomState.reset()
                }

            } else {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = scale
                )
            }
        }
    }
}

@Composable
fun TagContainer(
    modifier: Modifier = Modifier,
    text: String,
    onClickListener: () -> Unit
) {
    AssistChip(
        modifier = modifier,
        onClick = { onClickListener() },
        label = { Text(text = text) }
    )
}