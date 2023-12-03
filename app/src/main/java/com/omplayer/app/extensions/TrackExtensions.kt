package com.omplayer.app.extensions

import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import com.omplayer.app.entities.Album
import com.omplayer.app.entities.Artist
import com.omplayer.app.entities.Genre
import com.omplayer.app.db.entities.Track
import com.omplayer.app.utils.LibraryUtils

fun Track.toAlbum() = Album(
    id = albumId,
    title = album,
    artist = artist,
    cover = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toLong())
    } else {
        LibraryUtils.getAlbumCover(id)
    },
    year = year
)

fun Track.toArtist() = Artist(artist)

fun Track.toGenre() = Genre(genre)