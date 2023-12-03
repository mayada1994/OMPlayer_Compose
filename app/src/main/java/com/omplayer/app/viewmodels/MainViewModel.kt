package com.omplayer.app.viewmodels

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.omplayer.app.workers.LastFmOfflineScrobbledTracksWorker
import com.omplayer.app.workers.SyncWorker

class MainViewModel : BaseViewModel() {

    fun loadTracks(context: Context, observer: LifecycleOwner, isUpdate: Boolean = false) {
        WorkManager.getInstance(context).beginUniqueWork(
            SyncWorker::class.java.simpleName,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<SyncWorker>().build()
        ).apply {
            workInfosLiveData.observe(observer) {
                if (it.isNotEmpty()) {
                    when (it[0].state) {
                        WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> _showProgress.postValue(isUpdate)
                        else -> _showProgress.postValue(false)
                    }
                }
            }
            enqueue()
        }
    }

    fun checkForOfflineScrobbledTracks(context: Context) {
        WorkManager.getInstance(context).beginUniqueWork(
            LastFmOfflineScrobbledTracksWorker::class.java.simpleName,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<LastFmOfflineScrobbledTracksWorker>().setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        ).enqueue()
    }
}