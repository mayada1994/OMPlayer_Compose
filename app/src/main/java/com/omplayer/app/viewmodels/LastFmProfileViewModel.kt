package com.omplayer.app.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.network.responses.LastFmUserResponse.LastFmUser
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.utils.CacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LastFmProfileViewModel @Inject constructor(
    private val lastFmRepository: LastFmRepository,
    private val cacheManager: CacheManager
): BaseViewModel() {

    private val _userInfo = MutableLiveData<LastFmUser>()
    val userInfo: LiveData<LastFmUser> = _userInfo

    val isScrobblingEnabled = cacheManager.isScrobblingEnabledLiveData
    val username = cacheManager.currentLastFmSession?.name

    fun getUserInfo(context: Context) {
        viewModelScope.launch {
            _showProgress.postValue(true)
            lastFmRepository.getUserInfo(
                cacheManager.currentLastFmSession!!.name,
                context.getString(R.string.last_fm_api_key)
            )?.let {
                _userInfo.postValue(it.user)
            }
            _showProgress.postValue(false)
        }
    }

    fun changeScrobblingState(isEnabled: Boolean) {
        cacheManager.isScrobblingEnabled = isEnabled
    }

    fun logout() {
        cacheManager.currentLastFmSession = null
        cacheManager.isScrobblingEnabled = false
        _event.value = BaseViewEvent.NavigateUp
    }
}