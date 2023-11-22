package com.omplayer.app.repositories

import com.omplayer.app.db.dao.TrackDao
import com.omplayer.app.db.entities.Track
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(private val trackDao: TrackDao) {

    suspend fun getAllTracks(): List<Track>? {
        return try {
            trackDao.getAllTracks()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun insertAll(tracks: List<Track>) {
        try {
            trackDao.insertTracks(tracks)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteAll() {
        try {
            trackDao.deleteTracks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}