package com.omplayer.app.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.omplayer.app.R

@Composable
fun TrackItem(title: String, artist: String, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = colorResource(id = R.color.colorTextPrimary),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = artist,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            color = colorResource(id = R.color.colorTextPrimary)
        )
    }
}

@Preview
@Composable
fun PreviewTrackItem() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = colorResource(id = R.color.colorBackground)
    ) {
        TrackItem(
            "Army of the Night",
            "Powerwolf",
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        )
    }
}