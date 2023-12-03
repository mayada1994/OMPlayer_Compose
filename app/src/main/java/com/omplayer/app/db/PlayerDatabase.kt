package com.omplayer.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.omplayer.app.db.converters.TracklistConverter
import com.omplayer.app.db.dao.PlaylistDao
import com.omplayer.app.db.dao.ScrobbledTrackDao
import com.omplayer.app.db.dao.TrackDao
import com.omplayer.app.db.dao.VideoDao
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.db.entities.ScrobbledTrack
import com.omplayer.app.db.entities.Track
import com.omplayer.app.db.entities.Video

@Database(entities = [Playlist::class, ScrobbledTrack::class, Track::class, Video::class], version = 1)
@TypeConverters(TracklistConverter::class)
abstract class PlayerDatabase: RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao

    abstract fun scrobbledTrackDao(): ScrobbledTrackDao

    abstract fun trackDao(): TrackDao

    abstract fun videoDao(): VideoDao
}