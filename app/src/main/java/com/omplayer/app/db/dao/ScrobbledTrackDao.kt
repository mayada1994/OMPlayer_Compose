package com.omplayer.app.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omplayer.app.db.entities.ScrobbledTrack

@Dao
interface ScrobbledTrackDao {
    @Query("SELECT * FROM scrobbled_tracks")
    suspend fun getAllScrobbledTracks(): List<ScrobbledTrack>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertScrobbledTrack(scrobbledTrack: ScrobbledTrack)

    @Delete
    suspend fun deleteScrobbledTrack(scrobbledTrack: ScrobbledTrack)
}