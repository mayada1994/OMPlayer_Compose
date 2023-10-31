package com.omplayer.app.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val tracks: List<Int> = emptyList()
)