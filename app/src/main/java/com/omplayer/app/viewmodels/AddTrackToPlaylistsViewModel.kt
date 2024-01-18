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
class AddTrackToPlaylistsViewModel @Inject constructor(private val playlistRepository: PlaylistRepository) : BaseViewModel() {

    private var trackId: Int = -1
    private var initialSelectedPlaylists: List<Playlist> = emptyList()

    private val _selectedPlaylists: MutableLiveData<List<Playlist>> = MutableLiveData()
    val selectedPlaylists: LiveData<List<Playlist>> = _selectedPlaylists

    private val _playlists: MutableLiveData<List<Playlist>> = MutableLiveData()
    val playlists: LiveData<List<Playlist>> = _playlists

    fun init(trackId: Int) {
        this.trackId = trackId
        getPlaylists()
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            _showProgress.postValue(true)
            _playlists.value = playlistRepository.getAllPlaylists() ?: emptyList()
            initialSelectedPlaylists = _playlists.value?.filter { it.tracks.contains(trackId) } ?: emptyList()
            _selectedPlaylists.value = initialSelectedPlaylists
            _showProgress.postValue(false)
        }
    }

    fun onPlaylistSelected(playlist: Playlist) {
        _selectedPlaylists.value = if (selectedPlaylists.value?.contains(playlist) == true) {
            selectedPlaylists.value?.minus(playlist)
        } else {
            selectedPlaylists.value?.plus(playlist)
        }
    }

    fun addPlaylist(title: String?) {
        if (title.isNullOrBlank()) {
            _event.value = BaseViewEvent.ShowMessage(R.string.enter_valid_playlist_title)
            return
        }

        if (_playlists.value?.any { it.title.lowercase().trim() == title.lowercase().trim() } == true) {
            _event.value = BaseViewEvent.ShowMessage(R.string.playlist_duplication)
            return
        }

        viewModelScope.launch {
            _showProgress.postValue(true)
            playlistRepository.insert(Playlist(title = title))
            _playlists.value = playlistRepository.getAllPlaylists() ?: emptyList()
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
        initialSelectedPlaylists.subtract(_selectedPlaylists.value ?: emptyList()).let { removalPlaylists ->
            updatePlaylist(removalPlaylists.map { it.copy(tracks = it.tracks - trackId) })
        }
        _selectedPlaylists.value?.subtract(initialSelectedPlaylists).let { insertPlaylists ->
            updatePlaylist(insertPlaylists?.map { it.copy(tracks = it.tracks + trackId) } ?: emptyList())
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