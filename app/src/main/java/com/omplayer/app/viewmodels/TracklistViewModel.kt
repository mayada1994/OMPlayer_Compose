package com.omplayer.app.viewmodels

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.entities.Track
import com.omplayer.app.fragments.TracklistFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TracklistViewModel: BaseViewModel() {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun loadTracks(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID
            )
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Audio.Media.IS_MUSIC + " != 0",
                null,
                MediaStore.Audio.AudioColumns.ARTIST + "," + MediaStore.Audio.AudioColumns.ALBUM + " COLLATE NOCASE ASC"
            )?.let { cursor ->
                val extractedTracks = arrayListOf<Track>()
                try {
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {
                        val title = cursor.getString(0)
                        val artist = cursor.getString(1)
                        val path = cursor.getString(2)
                        val duration = cursor.getInt(3)
                        val position = cursor.getInt(4)
                        val album = cursor.getString(5)
                        val year = if (cursor.getString(6) != null) cursor.getString(6) else UNKNOWN
                        val id = cursor.getInt(7)
                        val albumId = cursor.getInt(8)
                        var genre = UNKNOWN
                        val albumCover = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId.toLong())

                        context.contentResolver.query(
                            MediaStore.Audio.Genres.getContentUriForAudioId("external", id),
                            arrayOf(MediaStore.Audio.Genres.NAME), null, null, null
                        )?.let { genresCursor ->
                            val genreColumnIndex =
                                genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

                            if (genresCursor.moveToFirst()) {
                                do {
                                    genre = genresCursor.getString(genreColumnIndex)
                                } while (genresCursor.moveToNext())
                            }
                            genresCursor.close()
                        }

                        cursor.moveToNext()

                        if (!path.isNullOrBlank() && formats.any { path.endsWith(it) }) {
                            extractedTracks.add(
                                Track(
                                    id = id,
                                    title = title,
                                    artist = artist,
                                    album = album,
                                    albumId = albumId,
                                    albumCover = albumCover,
                                    year = year,
                                    genre = genre,
                                    duration = duration,
                                    position = position,
                                    path = path
                                )
                            )
                        }
                    }

                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                } finally {
                    cursor.close()
                }
                _tracks.postValue(extractedTracks)
            }
        }
    }

    fun onTrackSelected(track: Track) {
        _event.value = BaseViewEvent.Navigate(TracklistFragmentDirections.navFromTracklistFragmentToPlayerFragment(track))
    }

    companion object {
        private val TAG = TracklistViewModel::class.java.simpleName
        private val formats = arrayOf(".aac", ".mp3", ".wav", ".ogg", ".midi", ".3gp", ".m4a", ".amr", ".flac")
        private const val UNKNOWN = "unknown"
    }
}