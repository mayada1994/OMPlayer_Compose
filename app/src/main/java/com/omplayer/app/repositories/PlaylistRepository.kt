package com.omplayer.app.repositories

import com.omplayer.app.db.dao.PlaylistDao
import com.omplayer.app.db.entities.Playlist
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(private val playlistDao: PlaylistDao) {

    suspend fun getAllPlaylists(): List<Playlist>? {
        return try {
            playlistDao.getAllPlaylists()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun insert(playlist: Playlist) {
        try {
            playlistDao.insertPlaylist(playlist)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insertAll(playlists: List<Playlist>) {
        try {
            playlistDao.insertPlaylists(playlists)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun delete(playlist: Playlist) {
        try {
            playlistDao.deletePlaylist(playlist)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}