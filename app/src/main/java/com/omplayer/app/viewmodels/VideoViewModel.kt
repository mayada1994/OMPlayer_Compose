package com.omplayer.app.viewmodels

import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
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
    fun getVideo(artist: String?, title: String?) {
        if (artist.isNullOrBlank() || title.isNullOrBlank()) {
            _event.value = Complex(
                BaseViewEvent.ShowError(R.string.general_error_message),
                CustomEvent.ShowPlaceholder
            )
            return
        }

        viewModelScope.launch {
            _showProgress.postValue(true)
            videoRepository.getVideoId(artist, title).let { videoId ->
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

    fun onStarClicked(artist: String?, title: String?, videoId: String) {
        //TODO: Save track
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}