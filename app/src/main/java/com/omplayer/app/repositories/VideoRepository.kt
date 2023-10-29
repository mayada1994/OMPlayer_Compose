package com.omplayer.app.repositories

import com.omplayer.app.db.dao.VideoDao
import com.omplayer.app.db.entities.Video
import com.omplayer.app.network.services.VideoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val videoDao: VideoDao,
    private val videoService: VideoService
) {

    companion object {
        private const val YOUTUBE_VIDEO_LINK = "https://www.youtube.com/watch?v="
    }

    // region DB

    suspend fun getVideos(): List<Video>? {
        return try {
            videoDao.getAllVideos()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getVideo(artist: String, title: String): Video? {
        return try {
            videoDao.getVideo(artist, title)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun saveVideo(video: Video) : Boolean {
        return try {
            videoDao.insertVideo(video)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun deleteVideo(video: Video) : Boolean {
        return try {
            videoDao.deleteVideo(video)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun handleVideoState(video: Video, isStarred: Boolean): Boolean {
        return if (isStarred) deleteVideo(video) else saveVideo(video)
    }
    // endregion

    // region Network

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
    // endregion
}