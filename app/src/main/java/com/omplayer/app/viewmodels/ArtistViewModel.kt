package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.omplayer.app.entities.Album
import com.omplayer.app.entities.Artist
import com.omplayer.app.extensions.toAlbum
import com.omplayer.app.fragments.ArtistFragmentDirections
import com.omplayer.app.utils.LibraryUtils

class ArtistViewModel: BaseViewModel() {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> = _albums

    fun init(artist: Artist?) {
        LibraryUtils.generalTracklist.value?.filter { it.artist == artist?.name }?.let {
            _albums.value = it.distinctBy { it.album }.map { it.toAlbum() }.sortedBy { it.year }
        }
    }

    fun onAlbumSelected(album: Album) {
        _event.value = BaseViewEvent.Navigate(ArtistFragmentDirections.navFromArtistFragmentToAlbumFragment(album))
    }

    fun playAllTracks(artist: Artist?) {
        LibraryUtils.generalTracklist.value?.filter { it.artist == artist?.name }?.let { artistTracklist ->
            if (artistTracklist.isNotEmpty() && artistTracklist != LibraryUtils.generalTracklist.value) {
                LibraryUtils.currentTracklist.value = artistTracklist
                LibraryUtils.currentTrack.value = artistTracklist[0]
                _event.value = BaseViewEvent.Navigate(ArtistFragmentDirections.navFromArtistFragmentToPlayerFragment())
            }
        }
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}