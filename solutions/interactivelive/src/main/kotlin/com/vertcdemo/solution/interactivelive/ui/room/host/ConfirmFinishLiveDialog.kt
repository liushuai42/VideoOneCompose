package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.vertcdemo.solution.interactivelive.R

@Composable
fun ConfirmFinishLiveDialog(
    onDismiss: (Boolean) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text(text = stringResource(R.string.end_live_title)) },
        text = { Text(text = stringResource(R.string.end_live_message)) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss(true)
            }) {
                Text(
                    text = stringResource(R.string.confirm),
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss(false)
            }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

@Preview
@Composable
fun ConfirmFinishLiveDialogPreview() {
    ConfirmFinishLiveDialog()
}
