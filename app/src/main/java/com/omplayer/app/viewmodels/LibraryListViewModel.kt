package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.omplayer.app.adapters.LibraryAdapter.LibraryListType
import com.omplayer.app.entities.Track

class LibraryListViewModel: BaseViewModel() {

    private val _playerList = MutableLiveData<List<Track>>()
    val playerList: LiveData<List<Track>> = _playerList

    fun initList(playerListTypePosition: Int) {
        // TODO: Add logic for different lists
        when (LibraryListType.getLibraryListTypeByPosition(playerListTypePosition)) {
            LibraryListType.SONGS -> {}
            LibraryListType.ARTISTS -> {}
            LibraryListType.ALBUMS -> {}
            LibraryListType.GENRES -> {}
        }
    }

}