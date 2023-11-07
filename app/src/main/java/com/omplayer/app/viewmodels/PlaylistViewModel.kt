package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.entities.Track
import com.omplayer.app.enums.PlaylistTracksAction
import com.omplayer.app.fragments.PlaylistFragmentDirections
import com.omplayer.app.repositories.PlaylistRepository
import com.omplayer.app.utils.LibraryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(private val playlistRepository: PlaylistRepository) : BaseViewModel() {

    private val _playlistTracks = MutableLiveData<List<Track>?>()
    val playlistTracks: LiveData<List<Track>?> = _playlistTracks

    fun getPlaylistTracks(playlistId: Int) {
        viewModelScope.launch {
            _showProgress.postValue(true)
            val playlist = playlistRepository.getPlaylistById(playlistId)

            _playlistTracks.value =
                if (playlist == null || playlist.tracks.isEmpty()) {
                    null
                } else {
                    LibraryUtils.generalTracklist.value?.filter { playlist.tracks.contains(it.id) }
                }
            _showProgress.postValue(false)
        }
    }

    fun onMenuItemClicked(menuItemId: Int, playlist: Playlist) {
        when (menuItemId) {
            R.id.addMenuItem -> _event.value = BaseViewEvent.Navigate(
                PlaylistFragmentDirections.navFromPlaylistFragmentToEditPlaylistFragment(
                    playlist,
                    PlaylistTracksAction.ADD.name
                )
            )

            R.id.removeMenuItem -> _event.value = BaseViewEvent.Navigate(
                PlaylistFragmentDirections.navFromPlaylistFragmentToEditPlaylistFragment(
                    playlist,
                    PlaylistTracksAction.REMOVE.name
                )
            )
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