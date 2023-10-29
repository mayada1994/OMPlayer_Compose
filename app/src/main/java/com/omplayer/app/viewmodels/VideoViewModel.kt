package com.omplayer.app.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.db.entities.Video
import com.omplayer.app.enums.ScrobbleMediaType
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.repositories.VideoRepository
import com.omplayer.app.utils.LibraryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val lastFmRepository: LastFmRepository,
    private val videoRepository: VideoRepository
) : BaseViewModel() {

    private var currentVideo: Video? = null
    private var isStarred: Boolean = false
    private var wasCurrentTrackScrobbled = false

    sealed class CustomEvent {
        data class PlayVideo(val videoId: String) : ViewEvent
        object ShowPlaceholder: ViewEvent
        data class UpdateBookmarkState(val isStarred: Boolean) : ViewEvent
    }

    companion object {
        private val TAG = VideoViewModel::class.java.simpleName
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
                    currentVideo = Video(
                        artist = artist,
                        title = title,
                        videoId = videoId
                    )

                    isStarred = videoRepository.getVideo(artist, title) != null

                    _event.postValue(Complex(
                        CustomEvent.UpdateBookmarkState(isStarred),
                        CustomEvent.PlayVideo(videoId)
                    ))
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

    fun isLocalTrack(): Boolean {
        return LibraryUtils.generalTracklist.value?.any {
            it.artist == currentVideo?.artist && it.title == currentVideo?.title
        } ?: false
    }

    fun changeBookmarkState() {
        viewModelScope.launch {
            _showProgress.postValue(true)
            videoRepository.handleVideoState(currentVideo!!, isStarred).let { updated ->
                if (updated) {
                    isStarred = !isStarred
                }
                _event.postValue(CustomEvent.UpdateBookmarkState(isStarred))
            }
            _showProgress.postValue(false)
        }
    }

    fun onPlaybackStarted(context: Context) {
        currentVideo?.let { updatePlayingTrack(it, context) }
    }

    fun handlePlaybackProgress(currentSecond: Float, duration: Float, context: Context) {
        currentVideo?.let {
            if (currentSecond < 1f) {
                wasCurrentTrackScrobbled = false // Handle video restart
            }

            if (shouldUpdateTrack()) {
                updatePlayingTrack(it, context)
            }

            if (shouldScrobbleTrack(currentSecond, duration)) {
                scrobbleTrack(it, context)
            }
        }
    }

    private fun updatePlayingTrack(video: Video, context: Context) {
        viewModelScope.launch {
            try {
                lastFmRepository.updatePlayingTrack(
                    video.artist,
                    video.title,
                    context.getString(R.string.last_fm_api_key),
                    context.getString(R.string.last_fm_secret)
                ).let {
                    it ?: return@launch

                    LibraryUtils.lastTrackUpdateOnLastFmTime = System.currentTimeMillis()
                    LibraryUtils.lastUpdatedMediaType = ScrobbleMediaType.VIDEO

                    Log.d(TAG, "updated $video")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun scrobbleTrack(video: Video, context: Context) {
        viewModelScope.launch {
            try {
                lastFmRepository.scrobbleTrack(
                    video.artist,
                    video.title,
                    LastFmRepository.timestamp,
                    context.getString(R.string.last_fm_api_key),
                    context.getString(R.string.last_fm_secret)
                ).let {
                    it ?: return@launch

                    wasCurrentTrackScrobbled = true

                    Log.d(TAG, "scrobbled $video")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun shouldUpdateTrack() =
        System.currentTimeMillis() - LibraryUtils.lastTrackUpdateOnLastFmTime >= LastFmRepository.LAST_FM_TRACK_UPDATE_INTERVAL
                || LibraryUtils.lastUpdatedMediaType != ScrobbleMediaType.VIDEO

    private fun shouldScrobbleTrack(currentSecond: Float, duration: Float) =
        !wasCurrentTrackScrobbled
                && duration >= TimeUnit.MILLISECONDS.toSeconds(LastFmRepository.LAST_FM_MIN_TRACK_DURATION)
                && (currentSecond >= TimeUnit.MILLISECONDS.toSeconds(LastFmRepository.LAST_FM_MAX_PLAYBACK_DURATION_BEFORE_SCROBBLE)
                || currentSecond / duration >= LastFmRepository.LAST_FM_SCROBBLING_PERCENTAGE)

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}