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
}