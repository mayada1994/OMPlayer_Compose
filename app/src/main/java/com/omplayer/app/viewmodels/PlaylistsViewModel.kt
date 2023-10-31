package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.repositories.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(private val playlistRepository: PlaylistRepository) : BaseViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>?>()
    val playlists: LiveData<List<Playlist>?> = _playlists

    fun getPlaylists() {
        viewModelScope.launch {
            _showProgress.postValue(true)
            _playlists.postValue(playlistRepository.getAllPlaylists())
            _showProgress.postValue(false)
        }
    }

    fun onPlaylistSelected(playlist: Playlist) {
        //TODO: Add navigation to playlist fragment
    }

    fun addPlaylist(title: String?) {
        if (title.isNullOrBlank()) {
            _event.value = BaseViewEvent.ShowError(R.string.enter_valid_playlist_title)
            return
        }
        viewModelScope.launch {
            _showProgress.postValue(true)
            playlistRepository.insert(Playlist(title = title))
            _playlists.postValue(playlistRepository.getAllPlaylists())
            _showProgress.postValue(false)
        }
    }

    fun renamePlaylist(playlist: Playlist, newTitle: String?) {
        if (newTitle.isNullOrBlank()) {
            _event.value = BaseViewEvent.ShowError(R.string.enter_valid_playlist_title)
            return
        }
        viewModelScope.launch {
            _showProgress.postValue(true)
            playlistRepository.insert(playlist.copy(title = newTitle))
            _playlists.postValue(playlistRepository.getAllPlaylists())
            _showProgress.postValue(false)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            _showProgress.postValue(true)
            playlistRepository.delete(playlist)
            _playlists.postValue(playlistRepository.getAllPlaylists())
            _showProgress.postValue(false)
        }
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}