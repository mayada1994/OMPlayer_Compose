package com.omplayer.app.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.omplayer.app.R

@Composable
fun DeletePlaylistDialog(
    onDelete: () -> Unit,
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
                .background(
                    color = colorResource(id = R.color.colorSelectedBackground),
                    CircleShape.copy(CornerSize(16.dp))
                )
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(id = R.string.delete_playlist_prompt),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(top = 16.dp),
                color = colorResource(id = R.color.colorTextPrimary),
                fontSize = 18.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 32.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onCancel() }) {
                    Text(
                        text = stringResource(id = R.string.cancel).uppercase(),
                        modifier = Modifier.wrapContentHeight(),
                        color = colorResource(id = R.color.colorTextPrimary),
                        fontWeight = FontWeight.Normal
                    )
                }

                TextButton(onClick = { onDelete() }) {
                    Text(
                        text = stringResource(id = R.string.ok).uppercase(),
                        modifier = Modifier
                            .wrapContentHeight()
                            .offset(x = 8.dp),
                        color = colorResource(id = R.color.colorTextPrimary),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DeletePlaylistDialogPreview() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = colorResource(id = R.color.colorBackground)
    ) {
        DeletePlaylistDialog(
            onDelete = {},
            onCancel = {},
            onDismiss = {}
        )
    }
}