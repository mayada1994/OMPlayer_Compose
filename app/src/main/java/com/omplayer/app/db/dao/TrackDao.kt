package com.omplayer.app.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omplayer.app.db.entities.Track

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks")
    suspend fun getAllTracks(): List<Track>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<Track>)

    @Query("DELETE FROM tracks")
    suspend fun deleteTracks()
}