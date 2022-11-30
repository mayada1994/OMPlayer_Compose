package com.omplayer.app.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.adapters.TracklistAdapter
import com.omplayer.app.databinding.FragmentTracklistBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.viewmodels.TracklistViewModel


class TracklistFragment : BaseMvvmFragment<FragmentTracklistBinding>(FragmentTracklistBinding::inflate) {

    override val viewModel: TracklistViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkExternalStoragePermission()

        viewModel.tracks.observe(viewLifecycleOwner) {
            (activity as MainActivity).setTracks(it)
            binding.rvTracklist.apply {
                adapter = TracklistAdapter(
                    it,
                    object : TracklistAdapter.OnTrackSelectedListener {
                        override fun onTrackSelected(track: Track) {
                            viewModel.onTrackSelected(track)
                            (activity as MainActivity).playTrack(track)
                        }
                    }
                )
                addItemDecoration(
                    DividerItemDecoration(
                        this.context,
                        DividerItemDecoration.VERTICAL
                    ).apply {
                        ContextCompat.getDrawable(context, R.drawable.line_divider)?.let { setDrawable(it) }
                    }
                )
            }
        }
    }

    private fun checkExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_STORAGE_PERMISSIONS_REQUEST
                )

            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_STORAGE_PERMISSIONS_REQUEST
                )
            }
        } else {
            viewModel.loadTracks(requireContext())
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadTracks(requireContext())
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 123
    }
}