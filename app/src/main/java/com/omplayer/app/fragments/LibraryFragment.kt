package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.omplayer.app.adapters.LibraryAdapter
import com.omplayer.app.adapters.LibraryAdapter.LibraryListType
import com.omplayer.app.databinding.FragmentLibraryBinding

class LibraryFragment : BaseFragment<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewPagerLibrary.adapter = LibraryAdapter(parentFragmentManager, lifecycle)

            TabLayoutMediator(tabsLibrary, viewPagerLibrary) { tab, position ->
                tab.text = getString(LibraryListType.getLibraryListTypeByPosition(position).titleRes)
            }.attach()
        }
    }
}