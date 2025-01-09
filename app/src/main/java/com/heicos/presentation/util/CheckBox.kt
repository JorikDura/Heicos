package com.heicos.presentation.util

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun CheckBox(
    modifier: Modifier = Modifier,
    checkedState: Boolean,
    text: String,
    onClickListener: () -> Unit
) {
    Crossfade(targetState = checkedState, label = "checkbox_animation") { checked ->
        Row(
            modifier = modifier
                .toggleable(
                    value = checkedState,
                    onValueChange = {
                        onClickListener()
                    },
                    role = Role.Checkbox
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}