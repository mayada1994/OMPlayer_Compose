package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.PlaylistAdapter
import com.omplayer.app.databinding.DialogAddChangePlaylistBinding
import com.omplayer.app.databinding.DialogDeletePlaylistBinding
import com.omplayer.app.databinding.FragmentPlaylistsBinding
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.viewmodels.PlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistsFragment : BaseMvvmFragment<FragmentPlaylistsBinding>(FragmentPlaylistsBinding::inflate) {

    override val viewModel: PlaylistsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylists()

        with(binding) {
            viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
                if (!playlists.isNullOrEmpty()) {
                    txtPlaceholder.isVisible = false
                    rvPlaylists.apply {
                        isVisible = true
                        adapter = PlaylistAdapter(items = playlists, listener = object : PlaylistAdapter.OnPlaylistSelectedListener() {
                            override fun onPlaylistSelected(playlist: Playlist) {
                                viewModel.onPlaylistSelected(playlist)
                            }

                            override fun onPlaylistRename(playlist: Playlist) {
                                showAddChangePlaylistDialog(playlist, playlists)
                            }

                            override fun onPlaylistDelete(playlist: Playlist) {
                                showDeletePlaylistDialog(playlist)
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
                }

                btnAdd.setOnClickListener { showAddChangePlaylistDialog(playlists = playlists) }
            }
            btnBack.setOnClickListener { viewModel.onBackPressed() }
        }
    }

    private fun showAddChangePlaylistDialog(playlist: Playlist? = null, playlists: List<Playlist>?) {
        val dialogBinding = DialogAddChangePlaylistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
        with(dialogBinding) {
            playlist?.title?.let {
                fPlaylistTitle.setText(it)
                fPlaylistTitle.requestFocus()
            }
            btnSave.setOnClickListener {
                if (playlist != null) {
                    viewModel.renamePlaylist(playlist, fPlaylistTitle.text.toString(), playlists)
                } else {
                    viewModel.addPlaylist(fPlaylistTitle.text.toString(), playlists)
                }
                alertDialog.dismiss()
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showDeletePlaylistDialog(playlist: Playlist) {
        val dialogBinding = DialogDeletePlaylistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
        with(dialogBinding) {
            btnOk.setOnClickListener {
                viewModel.deletePlaylist(playlist)
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