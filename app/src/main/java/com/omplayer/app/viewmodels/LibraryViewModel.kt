package com.omplayer.app.viewmodels

import com.omplayer.app.R
import com.omplayer.app.fragments.LibraryFragmentDirections
import com.omplayer.app.utils.CacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(private val cacheManager: CacheManager) : BaseViewModel() {

    fun onMenuItemClicked(menuItemId: Int) {
        when (menuItemId) {
            R.id.lastFmMenuItem -> _event.value = BaseViewEvent.Navigate(
                if (cacheManager.currentLastFmSession != null) {
                    LibraryFragmentDirections.navFromLibraryFragmentToLastFmProfileFragment()
                } else {
                    LibraryFragmentDirections.navFromLibraryFragmentToLastFmLoginFragment()
                }
            )

            R.id.bookmarkedMenuItem -> _event.value = BaseViewEvent.Navigate(
                LibraryFragmentDirections.navFromLibraryFragmentToBookmarkedVideosFragment()
            )

            R.id.playlistsMenuItem -> _event.value = BaseViewEvent.Navigate(
                LibraryFragmentDirections.navFromLibraryFragmentToPlaylistsFragment()
            )
        }
    }
}