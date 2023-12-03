package com.omplayer.app.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.omplayer.app.R
import com.omplayer.app.db.entities.ScrobbledTrack
import com.omplayer.app.db.entities.Track
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.utils.CacheManager
import com.omplayer.app.utils.ConnectivityUtils
import com.omplayer.app.utils.LibraryUtils.currentTrack
import com.omplayer.app.utils.LibraryUtils.wasCurrentTrackScrobbled
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class LastFmTrackScrobbleWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lastFmRepository: LastFmRepository,
    private val cacheManager: CacheManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        private val TAG = LastFmTrackScrobbleWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        val track = currentTrack.value

        track ?: return Result.failure()

        if (!cacheManager.isScrobblingEnabled) return Result.failure()

        return try {
            withContext(Dispatchers.IO) {
                if (!ConnectivityUtils.isOnline()) {

                    if (!wasCurrentTrackScrobbled) {
                        return@withContext scrobbleOfflineTrack(track)
                    }

                    return@withContext Result.failure()
                }

                scrobbleTrack(track)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            Result.failure()
        }
    }

    private suspend fun scrobbleTrack(track: Track) : Result {
        return try {
            lastFmRepository.scrobbleTrack(
                track.album,
                track.artist,
                track.title,
                LastFmRepository.timestamp,
                context.getString(R.string.last_fm_api_key),
                context.getString(R.string.last_fm_secret)
            ).let {
                wasCurrentTrackScrobbled = true

                it ?: return Result.failure()
            }

            Log.d(TAG, "scrobbled $track")

            Result.success()
        } catch (e :Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun scrobbleOfflineTrack(track: Track): Result {
        return if (lastFmRepository.insertScrobbledTrack(
                ScrobbledTrack(
                    artist = track.artist,
                    album = track.album,
                    title = track.title,
                    timestamp = LastFmRepository.timestamp
                )
            )
        ) {
            wasCurrentTrackScrobbled = true

            Log.d(TAG, "saved to offline scrobbled tracks $track")

            Result.success()
        } else {
            Result.failure()
        }
    }
}