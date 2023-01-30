package com.omplayer.app.repositories

import com.omplayer.app.network.responses.LastFmSessionResponse
import com.omplayer.app.network.responses.LastFmSimilarTracksResponse
import com.omplayer.app.network.services.LastFmService
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastFmRepository @Inject constructor(private val lastFmService: LastFmService) {

    companion object {
        private const val FORMAT = "json"
    }

    fun getLastFmSession(
        apiKey: String,
        password: String,
        username: String,
        api_sig: String
    ): LastFmSessionResponse {
        return lastFmService.getSession(apiKey, password, username, api_sig, FORMAT)
    }

    fun updatePlayingTrack(
        album: String,
        artist: String,
        track: String,
        apiKey: String,
        api_sig: String,
        sk: String
    ): ResponseBody {
        return lastFmService.updatePlayingTrack(album, artist, track, apiKey, api_sig, sk, FORMAT)
    }

    fun scrobbleTrack(
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

    fun loveTrack(
        artist: String,
        track: String,
        apiKey: String,
        api_sig: String,
        sk: String
    ): ResponseBody {
        return lastFmService.loveTrack(artist, track, apiKey, api_sig, sk, FORMAT)
    }

    fun getSimilarTracks(
        track: String,
        artist: String,
        apiKey: String
    ): LastFmSimilarTracksResponse {
        return lastFmService.getSimilarTracks(track, artist, apiKey, FORMAT)
    }
}