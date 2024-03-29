package com.omplayer.app.workers

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.omplayer.app.db.entities.Track
import com.omplayer.app.repositories.TrackRepository
import com.omplayer.app.utils.LibraryUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val trackRepository: TrackRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private val TAG = SyncWorker::class.java.simpleName
        private val formats = arrayOf(".aac", ".mp3", ".wav", ".ogg", ".midi", ".3gp", ".m4a", ".amr", ".flac")
        private const val UNKNOWN = "unknown"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            loadTracks(context)
        }
    }

    private suspend fun loadTracks(context: Context): Result {
        trackRepository.getAllTracks()?.let {
            if (it.isNotEmpty()) {
                LibraryUtils.generalTracklist.postValue(it)
                LibraryUtils.currentTracklist.postValue(it)
            }
        }

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
                    val year =
                        if (cursor.getString(6) != null) cursor.getString(6) else UNKNOWN
                    val id = cursor.getInt(7)
                    val albumId = cursor.getInt(8)
                    var genre = UNKNOWN

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

            if (extractedTracks.subtract((LibraryUtils.generalTracklist.value ?: emptyList()).toSet()).isEmpty()) {
                Log.d(TAG, "No new tracks found")
                return Result.success()
            }

            extractedTracks.sortedBy { it.title.lowercase() }.let {
                trackRepository.deleteAll()
                trackRepository.insertAll(it)
                LibraryUtils.generalTracklist.postValue(it)
                LibraryUtils.currentTracklist.postValue(it)
                Log.d(TAG, "Updated tracks")
            }
            return Result.success()
        }
        return Result.failure()
    }
}