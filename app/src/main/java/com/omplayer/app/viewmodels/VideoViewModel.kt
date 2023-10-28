package com.omplayer.app.viewmodels

import androidx.lifecycle.viewModelScope
import com.omplayer.app.entities.Track
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val lastFmRepository: LastFmRepository,
    private val videoRepository: VideoRepository
) : BaseViewModel() {

    sealed class CustomEvent {
        data class PlayVideo(val videoId: String) : ViewEvent
        object ShowPlaceholder: ViewEvent
    }
    fun getVideo(track: Track) {
        viewModelScope.launch {
            _showProgress.postValue(true)
            videoRepository.getVideoId(track.artist, track.title).let { videoId ->
                if (!videoId.isNullOrBlank()) {
                    _event.postValue(CustomEvent.PlayVideo(videoId))
                } else {
                    _event.postValue(CustomEvent.ShowPlaceholder)
                }
            }
            _showProgress.postValue(false)
        }
    }

    fun pauseCurrentTrack() {
        _event.value = BaseViewEvent.PausePlayback
    }

    fun onStarClicked(track: Track) {
        //TODO: Save track
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}