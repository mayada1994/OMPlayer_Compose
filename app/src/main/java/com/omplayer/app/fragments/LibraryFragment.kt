package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.omplayer.app.R
import com.omplayer.app.adapters.LibraryAdapter
import com.omplayer.app.adapters.LibraryAdapter.LibraryListType
import com.omplayer.app.databinding.FragmentLibraryBinding
import com.omplayer.app.viewmodels.LibraryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseMvvmFragment<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    override val viewModel: LibraryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewPagerLibrary.adapter = LibraryAdapter(childFragmentManager, lifecycle)

            TabLayoutMediator(tabsLibrary, viewPagerLibrary) { tab, position ->
                tab.text = getString(LibraryListType.getLibraryListTypeByPosition(position).titleRes)
            }.attach()

            btnMenu.setOnClickListener { showMenu(btnMenu) }
        }
    }

    private fun showMenu(view: View) {
        PopupMenu(requireContext(), view).let { popup ->
            popup.menuInflater.inflate(R.menu.library_menu, popup.menu)
            popup.setForceShowIcon(true)
            popup.setOnMenuItemClickListener { menuItem ->
                viewModel.onMenuItemClicked(menuItem.itemId)
                true
            }
            popup.show()
        }
    }
}