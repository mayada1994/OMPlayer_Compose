package com.omplayer.app.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "playlists", indices = [Index(value = ["id","title"], unique = true)])
@Parcelize
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val tracks: List<Int> = emptyList()
) : Parcelable