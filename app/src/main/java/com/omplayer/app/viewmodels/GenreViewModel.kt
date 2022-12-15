package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.omplayer.app.entities.Genre
import com.omplayer.app.entities.Track
import com.omplayer.app.fragments.GenreFragmentDirections
import com.omplayer.app.utils.LibraryUtils

class GenreViewModel: BaseViewModel() {

    private val _tracklist = MutableLiveData<List<Track>>()
    val tracklist: LiveData<List<Track>> = _tracklist

    fun init(genre: Genre?) {
        LibraryUtils.generalTracklist.value?.filter { it.genre == genre?.title }?.let {
            _tracklist.value = it
        }
    }

    fun onTrackSelected(track: Track) {
        if (LibraryUtils.generalTracklist.value == LibraryUtils.currentTracklist.value) {
            LibraryUtils.currentTracklist.value = tracklist.value
        }
        _event.value = BaseViewEvent.Navigate(GenreFragmentDirections.navFromGenreFragmentToPlayerFragment(track))
    }

}