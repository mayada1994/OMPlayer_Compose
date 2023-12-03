package com.omplayer.app.viewmodels

import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.db.entities.Track
import com.omplayer.app.enums.PlaylistTracksAction
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.repositories.PlaylistRepository
import com.omplayer.app.utils.LibraryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlaylistViewModel @Inject constructor(private val playlistRepository: PlaylistRepository) : BaseViewModel() {

    private lateinit var playlist: Playlist

    private var action: String? = null
    sealed class CustomEvent {
        data class SetTracks(val tracks: List<Track>?) : ViewEvent
    }

    fun init(playlist: Playlist, action: String) {
        this.playlist = playlist
        this.action = action

        updatePlaylist()
    }

    private fun updatePlaylist() {
        if (action == PlaylistTracksAction.ADD.name) {
            getNonPlaylistTracks()
        } else {
            getPlaylistTracks()
        }
    }

    private fun getPlaylistTracks() {
        viewModelScope.launch {
            _showProgress.postValue(true)
            _event.postValue(CustomEvent.SetTracks(
                if (playlist.tracks.isEmpty()) {
                    null
                } else {
                    LibraryUtils.generalTracklist.value?.filter { playlist.tracks.contains(it.id) }?.sortedBy { playlist?.tracks?.indexOf(it.id) }
                }))
            _showProgress.postValue(false)
        }
    }

    private fun getNonPlaylistTracks() {
        viewModelScope.launch {
            _showProgress.postValue(true)
            _event.postValue(CustomEvent.SetTracks(
                if (playlist.tracks.isEmpty()) {
                    LibraryUtils.generalTracklist.value?.sortedBy { it.title.lowercase() }
                } else {
                    LibraryUtils.generalTracklist.value?.filter {
                        playlist.tracks.contains(it.id).not()
                    }?.sortedBy { it.title.lowercase() }
                }))
            _showProgress.postValue(false)
        }
    }

    private fun addTracks(tracks: List<Int>) {
        viewModelScope.launch {
            _showProgress.postValue(true)
            playlist.copy(tracks = playlist.tracks + tracks).let { newPlaylist ->
                if (playlistRepository.insert(newPlaylist)) {
                    playlist = newPlaylist
                    updatePlaylist()
                } else {
                    _event.postValue(BaseViewEvent.ShowMessage(R.string.general_error_message))
                }
            }
            _showProgress.postValue(false)
        }
    }

    private fun removeTracks(tracks: List<Int>) {
        viewModelScope.launch {
            _showProgress.postValue(true)
            playlist.copy(tracks = playlist.tracks - tracks).let { newPlaylist ->
                if (playlistRepository.insert(newPlaylist)) {
                    playlist = newPlaylist
                    updatePlaylist()
                } else {
                    _event.postValue(BaseViewEvent.ShowMessage(R.string.general_error_message))
                }
            }
            _showProgress.postValue(false)
        }
    }

    fun saveTracks(tracks: List<Int>) {
        if (action == PlaylistTracksAction.ADD.name) {
            addTracks(tracks)
        } else {
            removeTracks(tracks)
        }
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}