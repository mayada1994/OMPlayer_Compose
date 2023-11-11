package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
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
                Glide.with(requireContext())
                    .load(it.images.firstOrNull { it.size == "extralarge" }?.url)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_last_fm_placeholder)
                    .into(imgUserAvatar)
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