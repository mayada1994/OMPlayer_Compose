package com.omplayer.app.network.services

import com.omplayer.app.entities.*
import com.omplayer.app.network.responses.LastFmSessionResponse
import com.omplayer.app.network.responses.LastFmSimilarTracksResponse
import com.omplayer.app.network.responses.LastFmUserResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LastFmService {

    @POST("?method=auth.getMobileSession")
    suspend fun getSession(
        @Query("api_key") apiKey: String,
        @Query("password") password: String,
        @Query("username") username: String,
        @Query("api_sig") apiSig: String,
        @Query("format") format: String
    ): LastFmSessionResponse

    @GET("?method=user.getInfo")
    suspend fun getUserInfo(
        @Query("user") user: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String
    ): LastFmUserResponse

    @POST("?method=track.updateNowPlaying")
    suspend fun updatePlayingTrack(
        @Query("album") album: String,
        @Query("artist") artist: String,
        @Query("track") track: String,
        @Query("api_key") apiKey: String,
        @Query("api_sig") apiSig: String,
        @Query("sk") sessionKey: String,
        @Query("format") format: String
    ): ResponseBody

    @POST("?method=track.scrobble")
    suspend fun scrobbleTrack(
        @Query("album") album: String,
        @Query("artist") artist: String,
        @Query("track") track: String,
        @Query("timestamp") timestamp: String,
        @Query("api_key") apiKey: String,
        @Query("api_sig") apiSig: String,
        @Query("sk") sessionKey: String,
        @Query("format") format: String
    ): ResponseBody

    @POST("?method=track.love")
    suspend fun loveTrack(
        @Query("artist") artist: String,
        @Query("track") track: String,
        @Query("api_key") apiKey: String,
        @Query("api_sig") apiSig: String,
        @Query("sk") sessionKey: String,
        @Query("format") format: String
    ): ResponseBody

    @GET("?method=track.getSimilar")
    suspend fun getSimilarTracks(
        @Query("track") track: String,
        @Query("artist") artist: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String
    ): LastFmSimilarTracksResponse

}