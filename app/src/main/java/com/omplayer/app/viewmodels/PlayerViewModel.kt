package com.omplayer.app.viewmodels

import com.omplayer.app.utils.LibraryUtils

class PlayerViewModel : BaseViewModel() {
    fun skipTrack(action: () -> Unit) {
        if (!LibraryUtils.isSingleTrackPlaylist()) {
            action()
        }
    }
}