package com.omplayer.app.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Int,
    val year: String,
    val genre: String,
    val duration: Int,
    val position: Int,
    val path: String
) : Parcelable
