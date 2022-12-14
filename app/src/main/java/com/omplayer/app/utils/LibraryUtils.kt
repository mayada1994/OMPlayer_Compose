package com.omplayer.app.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import androidx.lifecycle.MutableLiveData
import com.omplayer.app.entities.Track

object LibraryUtils {
    var tracklist = MutableLiveData<List<Track>>()
    var currentTrack = MutableLiveData<Track>()

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

    fun getAlbumCover(context: Context, uri: Uri): Bitmap? {
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

    fun playNextTrack() {
        currentTrack.value?.let { currentTrack.value = getNextTrack(it) }
    }

    fun playPreviousTrack() {
        currentTrack.value?.let { currentTrack.value = getPreviousTrack(it) }
    }

    private fun getNextTrack(track: Track): Track? {
        tracklist.value?.let {
            return if (it.last() == track) {
                it.first()
            } else {
                it[it.indexOf(track) + 1]
            }
        }
        return null
    }

    private fun getPreviousTrack(track: Track): Track? {
        tracklist.value?.let {
            return if (it.first() == track) {
                it.last()
            } else {
                it[it.indexOf(track) - 1]
            }
        }
        return null
    }

    fun isSingleTrackPlaylist() = (tracklist.value?.size ?: 0) < 2
}