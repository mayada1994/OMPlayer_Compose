package com.omplayer.app.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.omplayer.app.R

@Composable
fun AddChangePlaylistDialog(
    title: String,
    onSave: (String?) -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                color = colorResource(id = R.color.colorTextPrimary),
                fontSize = 18.sp
            )

            var playlistTitle by rememberSaveable { mutableStateOf("") }
            TextField(
                value = playlistTitle,
                onValueChange = { playlistTitle = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.hint_title),
                        color = colorResource(id = R.color.colorTextSecondary),
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .offset(y = (8).dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.transparent),
                    focusedIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                    unfocusedIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                    disabledIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                    textColor = colorResource(id = R.color.colorTextPrimary),
                    cursorColor = colorResource(id = R.color.colorTextPrimary)
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 32.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onCancel() }) {
                    Text(
                        text = stringResource(id = R.string.cancel).uppercase(),
                        modifier = Modifier.wrapContentHeight(),
                        color = colorResource(id = R.color.colorTextPrimary)
                    )
                }

                TextButton(onClick = { onSave(playlistTitle) }) {
                    Text(
                        text = stringResource(id = R.string.save).uppercase(),
                        modifier = Modifier
                            .wrapContentHeight()
                            .offset(x = 8.dp),
                        color = colorResource(id = R.color.colorTextPrimary)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AddChangePlaylistDialogPreview() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = colorResource(id = R.color.colorBackground)
    ) {
        AddChangePlaylistDialog(
            title = stringResource(id = R.string.enter_new_playlist_title),
            onSave = {},
            onCancel = {},
            onDismiss = {}
        )
    }
}