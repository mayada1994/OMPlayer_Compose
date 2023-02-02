package com.omplayer.app.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.omplayer.app.R
import com.omplayer.app.adapters.LibraryAdapter
import com.omplayer.app.adapters.LibraryAdapter.LibraryListType
import com.omplayer.app.databinding.DialogLastFmLoginBinding
import com.omplayer.app.databinding.FragmentLibraryBinding
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.viewmodels.LibraryViewModel
import com.omplayer.app.viewmodels.LibraryViewModel.LibraryViewEvent
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

            viewModel.isScrobblingEnabled.observe(viewLifecycleOwner) { scrobblingEnabled ->
                ImageViewCompat.setImageTintList(
                    toolbar.btnScrobble, ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(), if (scrobblingEnabled) {
                                R.color.colorAccent
                            } else {
                                R.color.colorBorderSeekBar
                            }
                        )
                    )
                )
            }

            toolbar.btnScrobble.setOnClickListener { viewModel.onScrobbleClick() }
        }
    }

    override fun handleCustomEvent(event: ViewEvent): Boolean {
        return when(event) {
            is LibraryViewEvent.ShowLoginDialog -> {
                showLoginDialog()
                true
            }
            else -> false
        }
    }

    private fun showLoginDialog() {
        with(DialogLastFmLoginBinding.inflate(layoutInflater)) {
            val dialog = AlertDialog.Builder(requireContext()).setView(root).create()
            btnLogin.setOnClickListener {
                viewModel.login(fUsername.text.toString(), fPassword.text.toString(), requireContext())
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}