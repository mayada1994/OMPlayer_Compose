package com.omplayer.app.extensions

import android.content.ContentUris
import android.provider.MediaStore
import com.omplayer.app.entities.Album
import com.omplayer.app.entities.Artist
import com.omplayer.app.entities.Genre
import com.omplayer.app.entities.Track

fun Track.toAlbum() = Album(
    id = albumId,
    title = album,
    artist = artist,
    cover = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toLong()),
    year = year
)

fun Track.toArtist() = Artist(artist)

fun Track.toGenre() = Genre(genre)