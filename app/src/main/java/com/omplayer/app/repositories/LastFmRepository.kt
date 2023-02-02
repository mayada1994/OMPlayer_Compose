package com.omplayer.app.repositories

import com.omplayer.app.network.responses.LastFmSessionResponse
import com.omplayer.app.network.responses.LastFmSimilarTracksResponse
import com.omplayer.app.network.services.LastFmService
import com.omplayer.app.utils.CacheManager
import okhttp3.ResponseBody
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmRepository @Inject constructor(
    private val lastFmService: LastFmService,
    private val cacheManager: CacheManager
) {

    companion object {
        private const val FORMAT = "json"

        fun md5(s: String): String? {
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
    }

    suspend fun getLastFmSession(
        apiKey: String,
        password: String,
        username: String,
        secret: String
    ): LastFmSessionResponse? {
        val apiSignature = md5("api_key" + apiKey + "methodauth.getMobileSessionpassword" + password + "username" + username + secret)
        apiSignature ?: return null

        return try {
            lastFmService.getSession(apiKey, password, username, apiSignature, FORMAT).also {
                cacheManager.currentLastFmSession = it.session
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updatePlayingTrack(
        album: String,
        artist: String,
        track: String,
        apiKey: String,
        api_sig: String,
        sk: String
    ): ResponseBody {
        return lastFmService.updatePlayingTrack(album, artist, track, apiKey, api_sig, sk, FORMAT)
    }

    suspend fun scrobbleTrack(
        album: String,
        artist: String,
        track: String,
        timestamp: String,
        apiKey: String,
        api_sig: String,
        sk: String
    ): ResponseBody {
        return lastFmService.scrobbleTrack(
            album,
            artist,
            track,
            timestamp,
            apiKey,
            api_sig,
            sk,
            FORMAT
        )
    }

    suspend fun loveTrack(
        artist: String,
        track: String,
        apiKey: String,
        api_sig: String,
        sk: String
    ): ResponseBody {
        return lastFmService.loveTrack(artist, track, apiKey, api_sig, sk, FORMAT)
    }

    suspend fun getSimilarTracks(
        track: String,
        artist: String,
        apiKey: String
    ): LastFmSimilarTracksResponse {
        return lastFmService.getSimilarTracks(track, artist, apiKey, FORMAT)
    }
}