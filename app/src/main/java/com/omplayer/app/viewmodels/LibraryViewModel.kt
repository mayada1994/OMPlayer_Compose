package com.omplayer.app.viewmodels

import androidx.lifecycle.MediatorLiveData
import com.omplayer.app.fragments.LibraryFragmentDirections
import com.omplayer.app.utils.CacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(private val cacheManager: CacheManager) : BaseViewModel() {

    val isScrobblingEnabled = MediatorLiveData<Boolean>().apply {
        addSource(cacheManager.isScrobblingEnabledLiveData) {
            value = it
        }
    }

    fun onScrobbleClick() {
        if (cacheManager.currentLastFmSession == null) {
            _event.value = BaseViewEvent.Navigate(LibraryFragmentDirections.navFromLibraryFragmentToLastFmLoginFragment())
            return
        }

        cacheManager.isScrobblingEnabled = !cacheManager.isScrobblingEnabled
    }
}