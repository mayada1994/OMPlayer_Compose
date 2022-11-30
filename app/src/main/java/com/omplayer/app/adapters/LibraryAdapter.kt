package com.omplayer.app.adapters

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.omplayer.app.R

class LibraryAdapter(
    private val fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    enum class LibraryList(@StringRes titleRes: Int, position: Int) {
        SONGS(R.string.songs, 0),
        ARTISTS(R.string.artists, 1),
        ALBUMS(R.string.albums, 2),
        GENRES(R.string.genres, 3)
    }

    override fun getItemCount(): Int = LibraryList.values().size

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }

}