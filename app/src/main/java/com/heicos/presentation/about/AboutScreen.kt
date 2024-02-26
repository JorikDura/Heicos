package com.heicos.presentation.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.heicos.R
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val context = LocalContext.current
        Spacer(modifier = Modifier.height(150.dp))
        Image(
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.me),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "This application — a parser of hentai-cosplays.com.",
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Intent(Intent.ACTION_VIEW).also {
                        it.data = Uri.parse("https://github.com/JorikDura")
                        context.startActivity(it)
                    }
                },
            text = "Made by chumyshhh[llI]",
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.light_blue)
        )
    }
}
