package com.omplayer.app.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scrobbled_tracks")
data class ScrobbledTrack(
    val artist: String,
    val album: String,
    val title: String,
    @PrimaryKey val timestamp: String
)