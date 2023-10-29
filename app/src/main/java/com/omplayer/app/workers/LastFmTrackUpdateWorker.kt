package com.omplayer.app.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.omplayer.app.R
import com.omplayer.app.enums.ScrobbleMediaType
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.utils.CacheManager
import com.omplayer.app.utils.LibraryUtils.currentTrack
import com.omplayer.app.utils.LibraryUtils.lastTrackUpdateOnLastFmTime
import com.omplayer.app.utils.LibraryUtils.lastUpdatedMediaType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class LastFmTrackUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val lastFmRepository: LastFmRepository,
    private val cacheManager: CacheManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        private val TAG = LastFmTrackUpdateWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        val track = currentTrack.value

        track ?: return Result.failure()

        if (!cacheManager.isScrobblingEnabled) return Result.failure()

        return try {
            withContext(Dispatchers.IO) {
                lastFmRepository.updatePlayingTrack(
                    track.album,
                    track.artist,
                    track.title,
                    context.getString(R.string.last_fm_api_key),
                    context.getString(R.string.last_fm_secret)
                ).let {
                    it ?: return@withContext Result.failure()

                    lastTrackUpdateOnLastFmTime = System.currentTimeMillis()
                    lastUpdatedMediaType = ScrobbleMediaType.TRACK
                }

                Log.d(TAG, "updated $track")

                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            Result.failure()
        }
    }
}