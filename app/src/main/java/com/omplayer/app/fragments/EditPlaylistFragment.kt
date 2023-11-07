package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.PlaylistTracksAdapter
import com.omplayer.app.databinding.FragmentEditPlaylistBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.viewmodels.EditPlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPlaylistFragment : BaseMvvmFragment<FragmentEditPlaylistBinding>(FragmentEditPlaylistBinding::inflate) {

    override val viewModel: EditPlaylistViewModel by viewModels()

    private val args: EditPlaylistFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(args.playlist, args.action)
    }

    override fun handleCustomEvent(event: ViewEvent): Boolean {
        return when (event) {
            is EditPlaylistViewModel.CustomEvent.SetTracks -> {
                updateTracklist(event.tracks)
                true
            }

            else -> super.handleCustomEvent(event)
        }
    }

    private fun updateTracklist(tracks: List<Track>?) {
        with(binding) {
            txtTitle.text = args.playlist.title
            btnBack.setOnClickListener { viewModel.onBackPressed() }

            if (!tracks.isNullOrEmpty()) {
                txtPlaceholder.isVisible = false
                btnSave.isVisible = true
                rvPlaylistTracks.apply {
                    isVisible = true
                    adapter = PlaylistTracksAdapter(tracks, object: PlaylistTracksAdapter.OnTracksSelectedListener {
                        override fun onTracksSelected(selectedTracks: List<Int>) {
                            btnSave.setOnClickListener { viewModel.saveTracks(selectedTracks) }
                        }
                    })
                    addItemDecoration(
                        DividerItemDecoration(
                            context,
                            DividerItemDecoration.VERTICAL
                        ).apply {
                            ContextCompat.getDrawable(context, R.drawable.line_divider)?.let { setDrawable(it) }
                        }
                    )
                }
            } else {
                txtPlaceholder.isVisible = true
                rvPlaylistTracks.isVisible = false
                btnSave.isVisible = false
            }
        }
    }
}