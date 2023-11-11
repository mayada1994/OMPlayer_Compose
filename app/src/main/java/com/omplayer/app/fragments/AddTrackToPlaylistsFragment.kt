package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.PlaylistAdapter
import com.omplayer.app.databinding.DialogAddChangePlaylistBinding
import com.omplayer.app.databinding.FragmentAddTrackToPlaylistsBinding
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.viewmodels.AddTrackToPlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTrackToPlaylistsFragment : BaseMvvmFragment<FragmentAddTrackToPlaylistsBinding>(FragmentAddTrackToPlaylistsBinding::inflate) {

    override val viewModel: AddTrackToPlaylistsViewModel by viewModels()

    private val args: AddTrackToPlaylistsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(args.trackId)

        with(binding) {
            btnAdd.setOnClickListener { showAddPlaylistDialog() }
            btnBack.setOnClickListener { viewModel.onBackPressed() }
        }
    }

    override fun handleCustomEvent(event: ViewEvent): Boolean {
        return when (event) {
            is AddTrackToPlaylistsViewModel.CustomEvent.SetPlaylists -> {
                updatePlaylists(event.playlists, event.selectedPlaylists)
                true
            }

            else -> super.handleCustomEvent(event)
        }
    }

    private fun updatePlaylists(playlists: List<Playlist>, selectedPlaylists: List<Playlist>) {
        with(binding) {
            if (playlists.isNotEmpty()) {
                txtPlaceholder.isVisible = false
                txtSelectedCount.text = getString(
                    R.string.selected,
                    resources.getQuantityString(
                        R.plurals.playlists_count,
                        selectedPlaylists.count(),
                        selectedPlaylists.count()
                    )
                )
                btnSave.apply {
                    isVisible = true
                    setOnClickListener { viewModel.onSaveClicked() }
                }
                rvPlaylists.apply {
                    isVisible = true
                    adapter = PlaylistAdapter(
                        isInEditMode = true,
                        items = playlists,
                        selectedItems = selectedPlaylists,
                        listener = object : PlaylistAdapter.OnPlaylistSelectedListener() {
                            override fun onPlaylistsSelected(playlists: List<Playlist>) {
                                txtSelectedCount.text = getString(
                                    R.string.selected,
                                    resources.getQuantityString(
                                        R.plurals.playlists_count,
                                        playlists.count(),
                                        playlists.count()
                                    )
                                )
                                viewModel.onPlaylistsSelected(playlists)
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
                rvPlaylists.isVisible = false
                btnSave.isVisible = false
            }
        }
    }

    private fun showAddPlaylistDialog() {
        val dialogBinding = DialogAddChangePlaylistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
        with(dialogBinding) {
            btnSave.setOnClickListener {
                viewModel.addPlaylist(fPlaylistTitle.text.toString())
                alertDialog.dismiss()
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}