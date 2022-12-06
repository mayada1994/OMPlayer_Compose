package com.omplayer.app.extensions

import com.omplayer.app.entities.Album
import com.omplayer.app.entities.Artist
import com.omplayer.app.entities.Genre
import com.omplayer.app.entities.Track

fun Track.toAlbum() = Album(
    id = albumId,
    title = album,
    artist = artist,
    cover = albumCover,
    year = year
)

fun Track.toArtist() = Artist(artist)

fun Track.toGenre() = Genre(genre)