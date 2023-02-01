package com.omplayer.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.omplayer.app.network.responses.LastFmSessionResponse.LastFmSession
import com.omplayer.app.network.responses.LastFmSessionResponse_LastFmSessionJsonAdapter
import com.squareup.moshi.Moshi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    private val context: Context,
    private val moshi: Moshi
) {

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    companion object {
        private const val IS_SCROBBLING_ENABLED: String = "is_scrobbling_enabled"
        private const val CURRENT_LAST_FM_SESSION: String = "current_last_fm_session"
    }

    var isScrobblingEnabled: Boolean
        get() = sharedPreferences.getBoolean(IS_SCROBBLING_ENABLED, false)
        set(value) = sharedPreferences.edit { putBoolean(IS_SCROBBLING_ENABLED, value) }

    var currentLastFmSession: LastFmSession?
        get() {
            sharedPreferences.getString(CURRENT_LAST_FM_SESSION, null).let {
                if (it.isNullOrBlank()) return null

                return try {
                    LastFmSessionResponse_LastFmSessionJsonAdapter(moshi).fromJson(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
        set(value) = sharedPreferences.edit {
            LastFmSessionResponse_LastFmSessionJsonAdapter(moshi).toJson(value)?.let {
                putString(CURRENT_LAST_FM_SESSION, it)
            }
        }
}