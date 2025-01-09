package com.heicos.presentation.settings

import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.heicos.R
import com.heicos.presentation.destinations.AboutScreenDestination
import com.heicos.presentation.settings.types.BackupTypes
import com.heicos.presentation.util.CheckBox
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun SettingsScreen(navigator: DestinationsNavigator) {
    val viewModel: SettingsScreenViewModel = hiltViewModel()

    val state = viewModel.state.collectAsState()

    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= 30) {
        if (!Environment.isExternalStorageManager()) {
            val managerDialog = remember {
                mutableStateOf(true)
            }

            if (managerDialog.value) {
                InfoDialog(
                    isOpen = managerDialog,
                    text = stringResource(R.string.external_storage_permission)
                )
            } else {
                Intent().apply {
                    action = android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                }.also {
                    context.startActivity(it)
                }
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(6.dp)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            LaunchedEffect(state.value) {
                if (state.value.isSuccess || state.value.isError) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (state.value.isSuccess) context.getString(R.string.success) else state.value.errorMessage,
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite
                        )
                        viewModel.onEvent(SettingsScreenEvents.ResetStatus)
                    }
                }
            }

            val infoDialog = remember {
                mutableStateOf(false)
            }

            if (infoDialog.value) {
                InfoDialog(
                    isOpen = infoDialog,
                    text = stringResource(R.string.backup_info)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.backup_settings),
                    fontSize = 24.sp
                )
                IconButton(
                    onClick = {
                        infoDialog.value = true
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                    })
            }

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Text(text = stringResource(R.string.view_download_history))

            Spacer(Modifier.height(12.dp))

            BackupElement(
                onBackupClick = {
                    viewModel.onEvent(SettingsScreenEvents.Backup(BackupTypes.ViewAndDownload))
                },
                onRestoreClick = {
                    viewModel.onEvent(SettingsScreenEvents.Restore(BackupTypes.ViewAndDownload))
                }
            )

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Text(text = stringResource(R.string.search_history_backup))

            Spacer(Modifier.height(12.dp))

            BackupElement(
                onBackupClick = {
                    viewModel.onEvent(SettingsScreenEvents.Backup(BackupTypes.Search))
                },
                onRestoreClick = {
                    viewModel.onEvent(SettingsScreenEvents.Restore(BackupTypes.Search))
                }
            )

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Text(
                text = stringResource(R.string.truncate),
                fontSize = 24.sp
            )

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Text(text = stringResource(R.string.view_download_history))

            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    viewModel.onEvent(SettingsScreenEvents.Truncate(BackupTypes.ViewAndDownload))
                }, content = {
                    Text(text = stringResource(R.string.clear))
                })

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Text(text = stringResource(R.string.search_history_backup))

            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    viewModel.onEvent(SettingsScreenEvents.Truncate(BackupTypes.Search))
                }, content = {
                    Text(text = stringResource(R.string.clear))
                })

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Text(
                text = stringResource(R.string.notifications),
                fontSize = 24.sp
            )

            var checkBoxState by remember {
                mutableStateOf(state.value.isNotificationEnabled)
            }

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            val notificationInfoDialog = remember {
                mutableStateOf(false)
            }

            if (notificationInfoDialog.value) {
                InfoDialog(
                    isOpen = notificationInfoDialog,
                    text = stringResource(R.string.the_changes_will_be_active_only_after_the_application_restart)
                )
            }

            CheckBox(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                checkedState = checkBoxState,
                text = stringResource(R.string.show_cosplays_images_download_notifications),
                onClickListener = {
                    checkBoxState = !checkBoxState
                    notificationInfoDialog.value = true
                    viewModel.onEvent(SettingsScreenEvents.ChangeNotificationSetting(checkBoxState))
                }
            )

            HorizontalDivider(Modifier.padding(vertical = 24.dp))

            Box {
                TextButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    onClick = {
                        navigator.navigate(AboutScreenDestination)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(id = R.string.about))
                    }
                }
            }
        }
    }


}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InfoDialog(
    isOpen: MutableState<Boolean>,
    text: String
) {
    BasicAlertDialog(
        onDismissRequest = {
            isOpen.value = false
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = text,
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = { isOpen.value = false },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.okay))
                }
            }
        }
    }
}

@Composable
fun BackupElement(
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier
                .weight(1f),
            onClick = {
                onBackupClick()
            },
            content = {
                Text(text = stringResource(R.string.backup))
            }
        )
        Button(
            modifier = Modifier
                .weight(1f),
            onClick = {
                onRestoreClick()
            },
            content = {
                Text(text = stringResource(R.string.restore))
            }
        )
    }
}