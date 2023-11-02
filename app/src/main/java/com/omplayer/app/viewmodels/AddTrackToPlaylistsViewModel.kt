package com.omplayer.app.viewmodels

import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.repositories.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTrackToPlaylistsViewModel @Inject constructor(private val playlistRepository: PlaylistRepository) : BaseViewModel() {

    private var trackId: Int = -1
    private var playlists: List<Playlist> = emptyList()
    private var initialSelectedPlaylists: List<Playlist> = emptyList()
    private var selectedPlaylists: List<Playlist> = emptyList()

    sealed class CustomEvent {
        data class SetPlaylists(val playlists: List<Playlist>, val selectedPlaylists: List<Playlist>) : ViewEvent
    }

    fun init(trackId: Int) {
        this.trackId = trackId
        getPlaylists()
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            _showProgress.postValue(true)
            playlists = playlistRepository.getAllPlaylists() ?: emptyList()
            initialSelectedPlaylists = playlists.filter { it.tracks.contains(trackId) }
            selectedPlaylists = initialSelectedPlaylists
            _event.postValue(
                CustomEvent.SetPlaylists(
                    playlists,
                    initialSelectedPlaylists
                )
            )
            _showProgress.postValue(false)
        }
    }

    fun onPlaylistsSelected(playlists: List<Playlist>) {
        selectedPlaylists = playlists
    }

    fun addPlaylist(title: String?) {
        if (title.isNullOrBlank()) {
            _event.value = BaseViewEvent.ShowMessage(R.string.enter_valid_playlist_title)
            return
        }

        if (playlists.any { it.title.lowercase().trim() == title.lowercase().trim() }) {
            _event.value = BaseViewEvent.ShowMessage(R.string.playlist_duplication)
            return
        }

        viewModelScope.launch {
            _showProgress.postValue(true)
            playlistRepository.insert(Playlist(title = title))
            playlists = playlistRepository.getAllPlaylists() ?: emptyList()
            _event.postValue(CustomEvent.SetPlaylists(playlists, selectedPlaylists))
            _showProgress.postValue(false)
        }
    }

    private fun updatePlaylist(playlists: List<Playlist>) {
        viewModelScope.launch {
            playlistRepository.updateAll(playlists)
        }
    }

    fun onSaveClicked() {
        _showProgress.value = true
        initialSelectedPlaylists.subtract(selectedPlaylists).let { removalPlaylists ->
            updatePlaylist(removalPlaylists.map { it.copy(tracks = it.tracks - trackId) })
        }
        selectedPlaylists.subtract(initialSelectedPlaylists).let { insertPlaylists ->
            updatePlaylist(insertPlaylists.map { it.copy(tracks = it.tracks + trackId) })
        }
        _event.value = Complex(
            BaseViewEvent.ShowMessage(R.string.playlists_updated),
            BaseViewEvent.NavigateUp
        )
        _showProgress.value = false
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}