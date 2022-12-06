package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.omplayer.app.databinding.FragmentLibraryListBinding
import com.omplayer.app.viewmodels.LibraryListViewModel

class LibraryListFragment : BaseMvvmFragment<FragmentLibraryListBinding>(FragmentLibraryListBinding::inflate) {

    override val viewModel: LibraryListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getInt(LIBRARY_LIST_TYPE_POSITION)?.let {
            viewModel.init(it)
            viewModel.libraryList.observe(viewLifecycleOwner) {
                // TODO: setup recycler view
            }
        }
    }

    companion object {
        private const val LIBRARY_LIST_TYPE_POSITION = "LIBRARY_LIST_TYPE_POSITION"

        @JvmStatic
        fun newInstance(libraryListTypePosition: Int) =
            LibraryListFragment().apply {
                arguments = Bundle().apply {
                    putInt(LIBRARY_LIST_TYPE_POSITION, libraryListTypePosition)
                }
            }
    }
}