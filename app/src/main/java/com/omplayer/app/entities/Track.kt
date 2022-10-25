package com.omplayer.app.entities

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: String
)
