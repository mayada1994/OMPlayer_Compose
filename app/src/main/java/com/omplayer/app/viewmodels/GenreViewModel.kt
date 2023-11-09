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
        LibraryUtils.generalTracklist.value?.filter { it.genre == genre?.title }?.let { tracks ->
            _tracklist.value = tracks.sortedWith(compareBy({ it.artist.lowercase() }, { it.title.lowercase() }))
        }
    }

    fun onTrackSelected(track: Track) {
        LibraryUtils.currentTracklist.value = tracklist.value
        LibraryUtils.currentTrack.value = track
        _event.value = BaseViewEvent.Navigate(GenreFragmentDirections.navFromGenreFragmentToPlayerFragment())
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }

}