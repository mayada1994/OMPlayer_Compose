package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.omplayer.app.entities.Album
import com.omplayer.app.entities.Track
import com.omplayer.app.fragments.AlbumFragmentDirections
import com.omplayer.app.utils.LibraryUtils

class AlbumViewModel: BaseViewModel() {

    private val _tracklist = MutableLiveData<List<Track>>()
    val tracklist: LiveData<List<Track>> = _tracklist

    fun init(album: Album?) {
        LibraryUtils.generalTracklist.value?.filter { it.albumId == album?.id }?.let {
            _tracklist.value = it.sortedBy { track -> track.position }
        }
    }

    fun onTrackSelected(track: Track) {
        if (LibraryUtils.generalTracklist.value == LibraryUtils.currentTracklist.value) {
            LibraryUtils.currentTracklist.value = tracklist.value
        }
        _event.value = BaseViewEvent.Navigate(AlbumFragmentDirections.navFromAlbumFragmentToPlayerFragment(track))
    }

}