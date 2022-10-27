package com.omplayer.app.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val year: String,
    val genre: String,
    val duration: Int,
    val position: Int,
    val path: String
) : Parcelable
