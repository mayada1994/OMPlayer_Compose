package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import coil.load
import coil.transform.CircleCropTransformation
import com.omplayer.app.R
import com.omplayer.app.databinding.FragmentLastFmProfileBinding
import com.omplayer.app.viewmodels.LastFmProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LastFmProfileFragment: BaseMvvmFragment<FragmentLastFmProfileBinding>(FragmentLastFmProfileBinding::inflate) {

    override val viewModel: LastFmProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserInfo(requireContext())

        with(binding) {
            txtUsername.text = viewModel.username

            viewModel.userInfo.observe(viewLifecycleOwner) {
                imgUserAvatar.load(it.images.firstOrNull { it.size == "extralarge" }?.url) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_last_fm_placeholder)
                    error(R.drawable.ic_last_fm_placeholder)
                }
            }

            viewModel.isScrobblingEnabled.distinctUntilChanged().observe(viewLifecycleOwner) {
                switchScrobbling.isChecked = it
            }
            switchScrobbling.setOnCheckedChangeListener { _, isChecked ->
                viewModel.changeScrobblingState(isChecked)
            }

            btnLogout.setOnClickListener { viewModel.logout() }
            btnBack.setOnClickListener { viewModel.onBackPressed() }
        }
    }
}