package com.omplayer.app.viewmodels

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.entities.Track
import com.omplayer.app.enums.PlaybackMode
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.fragments.PlayerFragmentDirections
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.utils.LibraryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(private val lastFmRepository: LastFmRepository) : BaseViewModel() {

    sealed class CustomEvent {
        data class UpdatePlaybackModeIcon(@DrawableRes val iconRes: Int) : ViewEvent
    }

    fun skipTrack(action: () -> Unit) {
        if (!LibraryUtils.isSingleTrackPlaylist()) {
            action()
        }
    }

    fun changePlaybackMode() {
        when (LibraryUtils.currentPlaybackMode) {
            PlaybackMode.LOOP_ALL -> {
                LibraryUtils.currentPlaybackMode = PlaybackMode.LOOP_SINGLE
                _event.value = CustomEvent.UpdatePlaybackModeIcon(R.drawable.ic_loop_single)
            }
            PlaybackMode.LOOP_SINGLE -> {
                LibraryUtils.currentPlaybackMode = PlaybackMode.SHUFFLE
                _event.value = CustomEvent.UpdatePlaybackModeIcon(R.drawable.ic_shuffle)
            }
            PlaybackMode.SHUFFLE -> {
                LibraryUtils.currentPlaybackMode = PlaybackMode.LOOP_ALL
                _event.value = CustomEvent.UpdatePlaybackModeIcon(R.drawable.ic_loop_all)
            }
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
                    PlayerFragmentDirections.navFromPlayerFragmentToVideoFragment(
                        artist = track.artist,
                        title = track.title,
                        isSimilarTrack = false
                    )
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