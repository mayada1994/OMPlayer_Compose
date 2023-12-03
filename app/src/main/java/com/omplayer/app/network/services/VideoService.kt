package com.omplayer.app.network.services

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface VideoService {

    @GET("{artist}/_/{track}")
    suspend fun getTrackPage(
        @Path("artist") artist: String,
        @Path("track") track: String
    ): ResponseBody
}