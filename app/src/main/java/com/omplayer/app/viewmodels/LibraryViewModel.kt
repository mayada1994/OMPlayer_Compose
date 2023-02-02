package com.omplayer.app.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.utils.CacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val lastFmRepository: LastFmRepository,
    private val cacheManager: CacheManager
) : BaseViewModel() {

    sealed class LibraryViewEvent {
        object ShowLoginDialog: ViewEvent
    }

    private val _isScrobblingEnabled = MutableLiveData(cacheManager.isScrobblingEnabled)
    val isScrobblingEnabled: LiveData<Boolean> = _isScrobblingEnabled

    fun onScrobbleClick() {
        if (cacheManager.currentLastFmSession == null) {
            _event.value = LibraryViewEvent.ShowLoginDialog
            return
        }

        isScrobblingEnabled.value?.let {
            cacheManager.isScrobblingEnabled = !it
            _isScrobblingEnabled.value = !it
        }
    }

    fun login(username: String?, password: String?, context: Context) {
        if (username.isNullOrBlank() || password.isNullOrBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            lastFmRepository.getLastFmSession(
                context.getString(R.string.last_fm_api_key),
                password,
                username,
                context.getString(R.string.last_fm_secret)
            )?.let {
                cacheManager.isScrobblingEnabled = true
                _isScrobblingEnabled.postValue(true)
            }
        }
    }
}