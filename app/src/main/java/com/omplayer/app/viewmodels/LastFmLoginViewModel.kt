package com.omplayer.app.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.utils.CacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LastFmLoginViewModel @Inject constructor(
    private val lastFmRepository: LastFmRepository,
    private val cacheManager: CacheManager
): BaseViewModel() {

    companion object {
        private val TAG = LastFmLoginViewModel::class.java.simpleName
    }

    fun login(username: String?, password: String?, context: Context) {
        when {
            username.isNullOrBlank() -> _event.value = BaseViewEvent.ShowMessage(R.string.enter_username)
            password.isNullOrBlank() -> _event.value = BaseViewEvent.ShowMessage(R.string.enter_password)
            else -> viewModelScope.launch(Dispatchers.IO) {
                _showProgress.postValue(true)
                try {
                    lastFmRepository.getLastFmSession(
                        context.getString(R.string.last_fm_api_key),
                        password,
                        username,
                        context.getString(R.string.last_fm_secret)
                    ).let { response ->
                        if (response != null) {
                            cacheManager.isScrobblingEnabled = true
                            _showProgress.postValue(false)
                            _event.postValue(BaseViewEvent.NavigateUp)
                        } else {
                            _event.postValue(BaseViewEvent.ShowMessage(R.string.general_error_message))
                            _showProgress.postValue(false)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                    _showProgress.postValue(false)
                    _event.postValue(BaseViewEvent.ShowMessage(R.string.general_error_message))
                }
            }
        }
    }

    fun register(context: Context) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.last.fm/join"))
            )
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}