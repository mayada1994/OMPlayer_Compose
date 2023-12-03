package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.db.entities.Video
import com.omplayer.app.fragments.BookmarkedVideosFragmentDirections
import com.omplayer.app.repositories.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkedVideosViewModel @Inject constructor(private val videoRepository: VideoRepository) : BaseViewModel() {

    private val _bookmarkedVideos = MutableLiveData<List<Video>?>()
    val bookmarkedVideos: LiveData<List<Video>?> = _bookmarkedVideos

    fun getBookmarkedVideos() {
        viewModelScope.launch {
            _showProgress.postValue(true)
            _bookmarkedVideos.postValue(videoRepository.getVideos())
            _showProgress.postValue(false)
        }
    }

    fun onVideoSelected(video: Video) {
        _event.value = BaseViewEvent.Navigate(
            BookmarkedVideosFragmentDirections.navFromBookmarkedVideosFragmentToVideoFragment(
                artist = video.artist,
                title = video.title,
                isSimilarTrack = true
            )
        )
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}