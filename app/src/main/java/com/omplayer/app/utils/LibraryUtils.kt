package com.omplayer.app.utils

import androidx.lifecycle.MutableLiveData
import com.omplayer.app.entities.Track

object LibraryUtils {
    var tracklist = MutableLiveData<List<Track>>()
}