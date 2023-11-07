package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.TracklistAdapter
import com.omplayer.app.databinding.FragmentPlaylistBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.viewmodels.PlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : BaseMvvmFragment<FragmentPlaylistBinding>(FragmentPlaylistBinding::inflate) {

    override val viewModel: PlaylistViewModel by viewModels()

    private val args: PlaylistFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylistTracks(args.playlist.id)

        with(binding) {
            txtTitle.text = args.playlist.title

            viewModel.playlistTracks.observe(viewLifecycleOwner) { playlistTracks ->
                if (!playlistTracks.isNullOrEmpty()) {
                    txtPlaceholder.isVisible = false
                    rvPlaylistTracks.apply {
                        isVisible = true
                        adapter = TracklistAdapter(
                            playlistTracks,
                            object : TracklistAdapter.OnTrackSelectedListener {
                                override fun onTrackSelected(track: Track) {
                                    viewModel.onTrackSelected(track)
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
                }
            }
            btnMenu.setOnClickListener { showMenu(btnMenu) }
            btnBack.setOnClickListener { viewModel.onBackPressed() }
        }
    }

    private fun showMenu(view: View) {
        PopupMenu(requireContext(), view).let { popup ->
            popup.menuInflater.inflate(R.menu.playlist_tracklist_menu, popup.menu)
            popup.setForceShowIcon(true)
            popup.setOnMenuItemClickListener { menuItem ->
                viewModel.onMenuItemClicked(menuItem.itemId, args.playlist)
                true
            }
            popup.show()
        }
    }
}