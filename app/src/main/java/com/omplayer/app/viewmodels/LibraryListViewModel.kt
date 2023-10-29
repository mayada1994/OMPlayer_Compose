package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.omplayer.app.R
import com.omplayer.app.adapters.LibraryAdapter.LibraryListType
import com.omplayer.app.entities.Album
import com.omplayer.app.entities.Artist
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
        libraryListTypePosition ?: return listOf()

        return when (LibraryListType.getLibraryListTypeByPosition(libraryListTypePosition!!)) {
            LibraryListType.SONGS -> tracklist.sortedWith(compareBy({ it.artist.lowercase() }, { it.title.lowercase() }))
            LibraryListType.ARTISTS -> tracklist.distinctBy { it.artist }.map { it.toArtist() }.sortedBy { it.name.lowercase() }
            LibraryListType.ALBUMS -> tracklist.distinctBy { it.albumId }.map { it.toAlbum() }.sortedBy { it.title.lowercase() }
            LibraryListType.GENRES -> tracklist.distinctBy { it.genre }.map { it.toGenre() }.sortedBy { it.title.lowercase() }
        }
    }

    fun onItemClick(item: Any) {
        _event.value = when (item) {
            is Track -> {
                LibraryUtils.currentTracklist.value = LibraryUtils.generalTracklist.value
                LibraryUtils.currentTrack.value = item
                BaseViewEvent.Navigate(LibraryFragmentDirections.navFromLibraryFragmentToPlayerFragment())
            }
            is Artist -> BaseViewEvent.Navigate(LibraryFragmentDirections.navFromLibraryFragmentToArtistFragment(item))
            is Album -> BaseViewEvent.Navigate(LibraryFragmentDirections.navFromLibraryFragmentToAlbumFragment(item))
            is Genre -> BaseViewEvent.Navigate(LibraryFragmentDirections.navFromLibraryFragmentToGenreFragment(item))
            else -> BaseViewEvent.ShowError(R.string.general_error_message)
        }
    }

}