package com.omplayer.app.db.entities

import androidx.room.Entity

@Entity(tableName = "videos", primaryKeys = ["artist", "title"])
data class Video(
    val videoId: String, // YouTube video id
    val artist: String,
    val title: String
)