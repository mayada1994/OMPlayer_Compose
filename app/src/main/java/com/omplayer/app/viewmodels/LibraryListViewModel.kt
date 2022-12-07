package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.omplayer.app.adapters.LibraryAdapter.LibraryListType
import com.omplayer.app.entities.Track
import com.omplayer.app.extensions.toAlbum
import com.omplayer.app.extensions.toArtist
import com.omplayer.app.extensions.toGenre
import com.omplayer.app.utils.LibraryUtils

class LibraryListViewModel: BaseViewModel() {

    private var libraryListTypePosition: Int? = null

    private val _libraryList = MediatorLiveData<List<Any>>().apply {
        addSource(LibraryUtils.tracklist) {
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
            LibraryListType.SONGS -> tracklist
            LibraryListType.ARTISTS -> tracklist.distinctBy { it.artist }.map { it.toArtist() }
            LibraryListType.ALBUMS -> tracklist.distinctBy { it.albumId }.map { it.toAlbum() }
            LibraryListType.GENRES -> tracklist.distinctBy { it.genre }.map { it.toGenre() }
        }
    }

    fun onItemClick(item: Any) {
        // TODO: Add logic
    }

}