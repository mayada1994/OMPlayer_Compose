package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.entities.Track
import com.omplayer.app.fragments.PlaylistFragmentDirections
import com.omplayer.app.utils.LibraryUtils
import kotlinx.coroutines.launch

class PlaylistViewModel : BaseViewModel() {

    private val _playlistTracks = MutableLiveData<List<Track>?>()
    val playlistTracks: LiveData<List<Track>?> = _playlistTracks

    fun getPlaylistTracks(playlist: Playlist) {
        viewModelScope.launch {
            _showProgress.postValue(true)
            _playlistTracks.value =
                if (playlist.tracks.isEmpty()) {
                    null
                } else {
                    LibraryUtils.generalTracklist.value?.filter { playlist.tracks.contains(it.id) }
                }
            _showProgress.postValue(false)
        }
    }

    fun onMenuItemClicked(menuItemId: Int) {
        when (menuItemId) {
            R.id.addMenuItem -> {}
            R.id.removeMenuItem -> {}
        }
    }

    fun onTrackSelected(track: Track) {
        LibraryUtils.currentTracklist.value = playlistTracks.value
        LibraryUtils.currentTrack.value = track
        _event.value = BaseViewEvent.Navigate(PlaylistFragmentDirections.navFromPlaylistFragmentToPlayerFragment())
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}