package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.omplayer.app.adapters.LibraryAdapter.LibraryListType
import com.omplayer.app.entities.Genre
import com.omplayer.app.entities.Track
import com.omplayer.app.extensions.toAlbum
import com.omplayer.app.extensions.toArtist
import com.omplayer.app.extensions.toGenre
import com.omplayer.app.fragments.LibraryFragmentDirections
import com.omplayer.app.utils.LibraryUtils

class LibraryListViewModel: BaseViewModel() {

    private var libraryListTypePosition: Int? = null

    private val _libraryList = MediatorLiveData<List<Any>>().apply {
        addSource(LibraryUtils.generalTracklist) {
            value = getCurrentList(it)
        }
    }
    val libraryList: LiveData<List<Any>> = _libraryList

    fun init(libraryListTypePosition: Int) {
        this.libraryListTypePosition = libraryListTypePosition
    }

    private fun getCurrentList(tracklist: List<Track>): List<Any> {
        if (libraryListTypePosition == null) return listOf()

        return when (LibraryListType.getLibraryListTypeByPosition(libraryListTypePosition!!)) {
            LibraryListType.SONGS -> tracklist.sortedWith(compareBy(Track::artist, Track::title))
            LibraryListType.ARTISTS -> tracklist.distinctBy { it.artist }.map { it.toArtist() }.sortedBy { it.name }
            LibraryListType.ALBUMS -> tracklist.distinctBy { it.albumId }.map { it.toAlbum() }.sortedBy { it.title }
            LibraryListType.GENRES -> tracklist.distinctBy { it.genre }.map { it.toGenre() }.sortedBy { it.title }
        }
    }

    fun onItemClick(item: Any) {
        // TODO: Add logic
        when (item) {
            is Track -> {
                if (LibraryUtils.generalTracklist.value != LibraryUtils.currentTracklist.value) {
                    LibraryUtils.currentTracklist.value = LibraryUtils.generalTracklist.value
                }
                _event.value = BaseViewEvent.Navigate(LibraryFragmentDirections.navFromLibraryFragmentToPlayerFragment(item))
            }
            is Genre -> _event.value = BaseViewEvent.Navigate(LibraryFragmentDirections.navFromLibraryFragmentToGenreFragment(item))
        }
    }

}