package com.omplayer.app.repositories

import com.omplayer.app.network.services.VideoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(private val videoService: VideoService) {

    companion object {
        private const val YOUTUBE_VIDEO_LINK = "https://www.youtube.com/watch?v="
    }

    suspend fun getVideoId(artist: String, track: String): String? {
        return try {
            videoService.getTrackPage(artist, track).string().let {
                return if (it.contains(YOUTUBE_VIDEO_LINK)) {
                    it.substringAfter(YOUTUBE_VIDEO_LINK).substringBefore("\"")
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}