package com.omplayer.app.repositories

import com.omplayer.app.db.dao.ScrobbledTrackDao
import com.omplayer.app.db.entities.ScrobbledTrack
import com.omplayer.app.network.responses.LastFmSessionResponse
import com.omplayer.app.network.responses.LastFmSimilarTracksResponse
import com.omplayer.app.network.responses.LastFmUserResponse
import com.omplayer.app.network.services.LastFmService
import com.omplayer.app.utils.CacheManager
import okhttp3.ResponseBody
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmRepository @Inject constructor(
    private val lastFmService: LastFmService,
    private val cacheManager: CacheManager,
    private val scrobbledTrackDao: ScrobbledTrackDao
) {

    companion object {
        private const val FORMAT = "json"
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val LAST_FM_MIN_TRACK_DURATION = TimeUnit.SECONDS.toMillis(30) // Don't scrobble track with less than 30 seconds duration
        val LAST_FM_MAX_PLAYBACK_DURATION_BEFORE_SCROBBLE = TimeUnit.MINUTES.toMillis(4) // For very long tracks
        val LAST_FM_TRACK_UPDATE_INTERVAL = TimeUnit.MINUTES.toMillis(3) // For very long tracks
        const val LAST_FM_SCROBBLING_PERCENTAGE = 0.5 // 50% of the track duration
    }

    // region DB

    suspend fun getAllScrobbledTracks(): List<ScrobbledTrack>? {
        return try {
            scrobbledTrackDao.getAllScrobbledTracks()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun insertScrobbledTrack(scrobbledTrack: ScrobbledTrack) : Boolean {
        return try {
            scrobbledTrackDao.insertScrobbledTrack(scrobbledTrack)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteScrobbledTrack(scrobbledTrack: ScrobbledTrack) {
        try {
            scrobbledTrackDao.deleteScrobbledTrack(scrobbledTrack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // endregion

    // region Network
    suspend fun getLastFmSession(
        apiKey: String,
        password: String,
        username: String,
        secret: String
    ): LastFmSessionResponse? {
        md5("api_key" + apiKey + "methodauth.getMobileSessionpassword" + password + "username" + username + secret).let { apiSignature ->
            if (apiSignature.isNullOrBlank()) return null

            return try {
                lastFmService.getSession(apiKey, password, username, apiSignature, FORMAT).also {
                    cacheManager.currentLastFmSession = it.session
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getUserInfo(user: String, apiKey: String): LastFmUserResponse? {
        return try {
            lastFmService.getUserInfo(user, apiKey, FORMAT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updatePlayingTrack(
        album: String?,
        artist: String,
        track: String,
        apiKey: String,
        secret: String
    ): ResponseBody? {
        val lastFmSessionKey = cacheManager.currentLastFmSession?.key

        md5("album" + album + "api_key" + apiKey + "artist" + artist + "methodtrack.updateNowPlaying" + "sk" + lastFmSessionKey + "track" + track + secret).let { apiSignature ->
            if (apiSignature.isNullOrBlank() || lastFmSessionKey.isNullOrBlank()) return null

            return lastFmService.updatePlayingTrack(
                album,
                artist,
                track,
                apiKey,
                apiSignature,
                lastFmSessionKey,
                FORMAT
            )
        }
    }

    suspend fun updatePlayingTrack(
        artist: String,
        track: String,
        apiKey: String,
        secret: String
    ): ResponseBody? {
        val lastFmSessionKey = cacheManager.currentLastFmSession?.key

        md5("api_key" + apiKey + "artist" + artist + "methodtrack.updateNowPlaying" + "sk" + lastFmSessionKey + "track" + track + secret).let { apiSignature ->
            if (apiSignature.isNullOrBlank() || lastFmSessionKey.isNullOrBlank()) return null

            return lastFmService.updatePlayingTrack(
                null,
                artist,
                track,
                apiKey,
                apiSignature,
                lastFmSessionKey,
                FORMAT
            )
        }
    }

    suspend fun scrobbleTrack(
        album: String,
        artist: String,
        track: String,
        timestamp: String,
        apiKey: String,
        secret: String
    ): ResponseBody? {
        val lastFmSessionKey = cacheManager.currentLastFmSession?.key

        md5("album" + album + "api_key" + apiKey + "artist" + artist + "methodtrack.scrobble" + "sk" + lastFmSessionKey + "timestamp" + timestamp + "track" + track + secret).let { apiSignature ->
            if (apiSignature.isNullOrBlank() || lastFmSessionKey.isNullOrBlank()) return null

            return lastFmService.scrobbleTrack(
                album,
                artist,
                track,
                timestamp,
                apiKey,
                apiSignature,
                lastFmSessionKey,
                FORMAT
            )
        }
    }

    suspend fun scrobbleTrack(
        artist: String,
        track: String,
        timestamp: String,
        apiKey: String,
        secret: String
    ): ResponseBody? {
        val lastFmSessionKey = cacheManager.currentLastFmSession?.key

        md5("api_key" + apiKey + "artist" + artist + "methodtrack.scrobble" + "sk" + lastFmSessionKey + "timestamp" + timestamp + "track" + track + secret).let { apiSignature ->
            if (apiSignature.isNullOrBlank() || lastFmSessionKey.isNullOrBlank()) return null

            return lastFmService.scrobbleTrack(
                null,
                artist,
                track,
                timestamp,
                apiKey,
                apiSignature,
                lastFmSessionKey,
                FORMAT
            )
        }
    }

    suspend fun loveTrack(
        artist: String,
        track: String,
        apiKey: String,
        secret: String
    ): ResponseBody? {
        val lastFmSessionKey = cacheManager.currentLastFmSession?.key

        md5("api_key" + apiKey + "artist" + artist + "methodtrack.love" + "sk" + lastFmSessionKey + "track" + track + secret).let { apiSignature ->
            if (apiSignature.isNullOrBlank() || lastFmSessionKey.isNullOrBlank()) return null

            return lastFmService.loveTrack(
                artist,
                track,
                apiKey,
                apiSignature,
                lastFmSessionKey,
                FORMAT
            )
        }
    }

    suspend fun getSimilarTracks(
        track: String,
        artist: String,
        apiKey: String
    ): LastFmSimilarTracksResponse {
        return lastFmService.getSimilarTracks(track, artist, apiKey, FORMAT)
    }

    private fun md5(s: String): String? {
        return try {
            val bytes = MessageDigest.getInstance("MD5").digest(s.toByteArray(charset("UTF-8")))
            val b = StringBuilder(32)
            for (aByte in bytes) {
                val hex = Integer.toHexString(aByte.toInt() and 0xFF)
                if (hex.length == 1)
                    b.append('0')
                b.append(hex)
            }
            b.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    // endregion
}