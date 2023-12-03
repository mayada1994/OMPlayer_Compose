package com.omplayer.app.viewmodels

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.omplayer.app.R
import com.omplayer.app.fragments.LibraryFragmentDirections
import com.omplayer.app.utils.CacheManager
import com.omplayer.app.workers.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(private val cacheManager: CacheManager) : BaseViewModel() {

    fun onMenuItemClicked(menuItemId: Int, context: Context, observer: LifecycleOwner) {
        when (menuItemId) {
            R.id.lastFmMenuItem -> _event.value = BaseViewEvent.Navigate(
                if (cacheManager.currentLastFmSession != null) {
                    LibraryFragmentDirections.navFromLibraryFragmentToLastFmProfileFragment()
                } else {
                    LibraryFragmentDirections.navFromLibraryFragmentToLastFmLoginFragment()
                }
            )

            R.id.bookmarkedMenuItem -> _event.value = BaseViewEvent.Navigate(
                LibraryFragmentDirections.navFromLibraryFragmentToBookmarkedVideosFragment()
            )

            R.id.playlistsMenuItem -> _event.value = BaseViewEvent.Navigate(
                LibraryFragmentDirections.navFromLibraryFragmentToPlaylistsFragment()
            )
            R.id.syncMenuItem -> {
                WorkManager.getInstance(context).beginUniqueWork(
                    SyncWorker::class.java.simpleName,
                    ExistingWorkPolicy.KEEP,
                    OneTimeWorkRequestBuilder<SyncWorker>().build()
                ).apply {
                    workInfosLiveData.observe(observer) {
                        if (it.isNotEmpty()) {
                            when (it[0].state) {
                                WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> _showProgress.postValue(true)
                                else -> _showProgress.postValue(false)
                            }
                        }
                    }
                    enqueue()
                }
            }
        }
    }
}