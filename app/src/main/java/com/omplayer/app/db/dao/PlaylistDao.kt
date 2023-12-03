package com.omplayer.app.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.omplayer.app.db.entities.Playlist

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<Playlist>?

    @Query("SELECT * FROM playlists WHERE id=:id")
    suspend fun getPlaylistById(id: Int): Playlist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<Playlist>)

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Update
    suspend fun updatePlaylists(playlists: List<Playlist>)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)
}