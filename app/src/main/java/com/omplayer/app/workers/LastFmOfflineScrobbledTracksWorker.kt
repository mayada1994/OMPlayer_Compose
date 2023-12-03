package com.omplayer.app.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.omplayer.app.R
import com.omplayer.app.db.entities.ScrobbledTrack
import com.omplayer.app.repositories.LastFmRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class LastFmOfflineScrobbledTracksWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lastFmRepository: LastFmRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private val TAG = LastFmOfflineScrobbledTracksWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.IO) {
                lastFmRepository.getAllScrobbledTracks().let { scrobbledTracks ->
                    if (scrobbledTracks.isNullOrEmpty()) {
                        Log.d(TAG, "No offline scrobbled tracks found")
                        return@withContext Result.success()
                    }

                    scrobbledTracks.forEach {
                        scrobbleTrack(it)
                        delay(TimeUnit.SECONDS.toMillis(3))
                    }

                    Result.success()
                }
            }
        }  catch (e: Exception) {
            Log.e(TAG, e.message, e)
            Result.failure()
        }
    }

    private suspend fun scrobbleTrack(track: ScrobbledTrack) : Result {
        return try {
            lastFmRepository.scrobbleTrack(
                track.album,
                track.artist,
                track.title,
                track.timestamp,
                context.getString(R.string.last_fm_api_key),
                context.getString(R.string.last_fm_secret)
            ).let {
                it ?: return Result.failure()
            }

            Log.d(TAG, "scrobbled $track")

            try {
                lastFmRepository.deleteScrobbledTrack(track)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Result.success()
        } catch (e :Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}