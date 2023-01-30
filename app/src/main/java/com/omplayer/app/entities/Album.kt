package com.omplayer.app.entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id: Int,
    val title: String,
    val artist: String,
    val cover: Uri?,
    val year: String,
) : Parcelable