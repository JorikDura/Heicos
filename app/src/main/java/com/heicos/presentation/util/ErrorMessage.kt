package com.heicos.presentation.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.heicos.R

@JvmName("ErrorMessage")
@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    message: String,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = message
        )
    }
}

@JvmName("ErrorMessageWithButton")
@Composable
fun ErrorMessage(
    modifier: Modifier = Modifier,
    message: String,
    onButtonClickListener: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = message
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .clickable { onButtonClickListener() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.try_again)
            )
        }
    }
}