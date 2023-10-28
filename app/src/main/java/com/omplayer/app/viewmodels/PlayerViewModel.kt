package com.omplayer.app.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.entities.Track
import com.omplayer.app.fragments.PlayerFragmentDirections
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.utils.LibraryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(private val lastFmRepository: LastFmRepository) : BaseViewModel() {
    fun skipTrack(action: () -> Unit) {
        if (!LibraryUtils.isSingleTrackPlaylist()) {
            action()
        }
    }

    fun onMenuItemClicked(menuItemId: Int, context: Context) {
        LibraryUtils.currentTrack.value.let { track ->
            if (track == null) {
                _event.value = BaseViewEvent.ShowError(R.string.general_error_message)
                return
            }

            when (menuItemId) {
                R.id.loveMenuItem -> addCurrentTrackToLoved(context, track)

                R.id.similarTracksMenuItem -> _event.value = BaseViewEvent.Navigate(
                    PlayerFragmentDirections.navFromPlayerFragmentToSimilarTracksFragment(track)
                )

                R.id.videoMenuItem -> _event.value = BaseViewEvent.Navigate(
                    PlayerFragmentDirections.navFromPlayerFragmentToVideoFragment(track)
                )
            }
        }
    }

    private fun addCurrentTrackToLoved(context: Context, track: Track) {
        viewModelScope.launch {
            try {
                _showProgress.postValue(true)
                lastFmRepository.loveTrack(
                    artist = track.artist,
                    track = track.title,
                    apiKey = context.getString(R.string.last_fm_api_key),
                    secret = context.getString(R.string.last_fm_secret)
                )
                _showProgress.postValue(false)
                _event.postValue(BaseViewEvent.ShowError(R.string.track_added_to_loved))
            } catch (e: Exception) {
                e.printStackTrace()
                _showProgress.postValue(false)
                _event.postValue(BaseViewEvent.ShowError(R.string.general_error_message))
            }
        }
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}