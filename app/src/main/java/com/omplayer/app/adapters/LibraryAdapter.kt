package com.omplayer.app.adapters

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.omplayer.app.R
import com.omplayer.app.fragments.LibraryListFragment

class LibraryAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    enum class LibraryListType(@StringRes val titleRes: Int, val position: Int) {
        SONGS(R.string.songs, 0),
        ARTISTS(R.string.artists, 1),
        ALBUMS(R.string.albums, 2),
        GENRES(R.string.genres, 3);

        companion object {
            fun getLibraryListTypeByPosition(position: Int): LibraryListType = values().find { it.position == position } ?: SONGS
        }
    }

    override fun getItemCount(): Int = LibraryListType.values().size

    override fun createFragment(position: Int): Fragment = LibraryListFragment.newInstance(position)

}