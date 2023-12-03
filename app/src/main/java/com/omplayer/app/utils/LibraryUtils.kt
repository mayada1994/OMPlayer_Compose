package com.omplayer.app.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.omplayer.app.db.entities.Track
import com.omplayer.app.enums.PlaybackMode
import com.omplayer.app.enums.ScrobbleMediaType

object LibraryUtils {
    var generalTracklist = MutableLiveData<List<Track>>()
    var currentTracklist = MutableLiveData<List<Track>>()
    var currentTrack = MutableLiveData<Track>()
    var currentTrackProgress = MutableLiveData<Long>()
    var currentPlaybackMode = PlaybackMode.LOOP_ALL
    var wasCurrentTrackScrobbled = false
    var lastTrackUpdateOnLastFmTime = 0L
    var lastUpdatedMediaType: ScrobbleMediaType? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAlbumCover(context: Context, trackId: Int): Bitmap? {
        return try {
            context.contentResolver.loadThumbnail(
                ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    trackId.toLong()
                ),
                Size(512, 512),
                null
            )
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAlbumCover(context: Context, uri: Uri?): Bitmap? {
        uri ?: return null

        return try {
            context.contentResolver.loadThumbnail(
                uri,
                Size(512, 512),
                null
            )
        } catch (e: Exception) {
            null
        }
    }

    // TODO: Check on older Android apis
    fun getAlbumCover(trackId: Int): Uri? {
        return try {
            ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                trackId.toLong()
            )
        } catch (e: Exception) {
            null
        }
    }

    fun playNextTrack() {
        currentTrack.value?.let { currentTrack.value = getNextTrack(it) }
    }

    fun playPreviousTrack() {
        currentTrack.value?.let { currentTrack.value = getPreviousTrack(it) }
    }

    private fun getNextTrack(track: Track): Track? {
        currentTracklist.value?.let {
            return when (currentPlaybackMode) {
                PlaybackMode.LOOP_ALL -> {
                    if (it.last() == track) {
                        it.first()
                    } else {
                        it[it.indexOf(track) + 1]
                    }
                }
                PlaybackMode.LOOP_SINGLE -> currentTrack.value
                PlaybackMode.SHUFFLE -> it.random()
            }
        }
        return null
    }

    private fun getPreviousTrack(track: Track): Track? {
        currentTracklist.value?.let {
            return when (currentPlaybackMode) {
                PlaybackMode.LOOP_ALL -> {
                    if (it.first() == track) {
                        it.last()
                    } else {
                        it[it.indexOf(track) - 1]
                    }
                }
                PlaybackMode.LOOP_SINGLE -> currentTrack.value
                PlaybackMode.SHUFFLE -> it.random()
            }
        }
        return null
    }

    fun isSingleTrackPlaylist() = (currentTracklist.value?.size ?: 0) < 2
}