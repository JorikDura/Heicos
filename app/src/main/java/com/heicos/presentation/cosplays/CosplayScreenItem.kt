package com.heicos.presentation.cosplays

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.heicos.R
import com.heicos.domain.model.CosplayPreview
import com.heicos.presentation.destinations.FullCosplayScreenDestination
import com.heicos.presentation.util.USER_AGENT_MOZILLA
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun CosplayScreenItem(
    modifier: Modifier = Modifier,
    cosplay: CosplayPreview,
    navigator: DestinationsNavigator
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable {
                navigator.navigate(
                    FullCosplayScreenDestination(
                        cosplayPreview = cosplay
                    )
                )
            }
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp)
    ) {
        Box(modifier = Modifier.height(275.dp)) {
            SubcomposeAsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cosplay.previewUrl)
                    .addHeader("User-Agent", USER_AGENT_MOZILLA)
                    .crossfade(true)
                    .build(),
                contentDescription = cosplay.title,
                contentScale = ContentScale.Crop,
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black
                            ),
                            startY = 450f
                        )
                    )
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                text = cosplay.title,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}