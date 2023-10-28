package com.omplayer.app.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.omplayer.app.R
import com.omplayer.app.entities.Track
import com.omplayer.app.fragments.SimilarTracksFragmentDirections
import com.omplayer.app.network.responses.LastFmSimilarTracksResponse.LastFmSimilarTrack
import com.omplayer.app.repositories.LastFmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimilarTracksViewModel @Inject constructor(private val lastFmRepository: LastFmRepository) :
    BaseViewModel() {

    private val _similarTracks = MutableLiveData<List<LastFmSimilarTrack>?>()
    val similarTracks: LiveData<List<LastFmSimilarTrack>?> = _similarTracks

    fun getSimilarTracks(track: Track?, context: Context) {
        if (track == null) {
            _similarTracks.value = null
            return
        }

        viewModelScope.launch {
            try {
                _showProgress.postValue(true)
                lastFmRepository.getSimilarTracks(
                    track = track.title,
                    artist = track.artist,
                    apiKey = context.getString(R.string.last_fm_api_key)
                ).similarTracks.similarTracksList.let {
                    _similarTracks.postValue(it)
                }
                _showProgress.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _showProgress.postValue(false)
                _similarTracks.postValue(null)
                _event.postValue(BaseViewEvent.ShowError(R.string.general_error_message))
            }
        }
    }

    fun onTrackSelected(similarTrack: LastFmSimilarTrack) {
        _event.value = BaseViewEvent.Navigate(
            SimilarTracksFragmentDirections.navFromSimilarTracksFragmentToVideoFragment(
                artist = similarTrack.artist.name,
                title = similarTrack.name,
                isSimilarTrack = true
            )
        )
    }

    fun onBackPressed() {
        _event.value = BaseViewEvent.NavigateUp
    }
}