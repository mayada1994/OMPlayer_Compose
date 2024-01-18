package com.omplayer.app.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omplayer.app.R

@Composable
fun PlaylistItem(title: String, songsCount: Int, modifier: Modifier, isInEditMode: Boolean = false, onMenuClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                color = colorResource(id = R.color.colorTextPrimary),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = songsCount.toString(),
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                color = colorResource(id = R.color.colorTextSecondary),
                fontSize = 12.sp
            )
        }

        if (!isInEditMode) {
            IconButton(
                onClick = { onMenuClicked() },
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    tint = colorResource(id = R.color.colorTextPrimary),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewPlaylistItem() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = colorResource(id = R.color.colorBackground)
    ) {
        PlaylistItem(
            "Favorites",
            35,
            Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            false,
            {}
        )
    }
}