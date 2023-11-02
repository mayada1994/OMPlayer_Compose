package com.omplayer.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.omplayer.app.R
import com.omplayer.app.databinding.ItemPlaylistBinding
import com.omplayer.app.db.entities.Playlist

class PlaylistAdapter(
    private val isInEditMode: Boolean = false,
    private val items: List<Playlist>,
    selectedItems: List<Playlist> = emptyList(),
    private val listener: OnPlaylistSelectedListener
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    private val selectedPlaylists: ArrayList<Playlist> = arrayListOf()

    init {
        selectedPlaylists.addAll(selectedItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PlaylistViewHolder(private val viewBinding: ItemPlaylistBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(playlist: Playlist) {
            with(viewBinding) {
                txtTitle.text = playlist.title
                playlist.tracks.count().let {
                    txtSongsCount.text = root.context.resources.getQuantityString(R.plurals.songs_count, it, it)
                }
                btnMenu.isVisible = !isInEditMode
                btnMenu.setOnClickListener { showMenu(btnMenu, playlist) }

                root.apply {
                    isSelected = selectedPlaylists.contains(playlist) // Set initial state
                    setOnClickListener {
                        if (this@PlaylistAdapter.isInEditMode) {
                            if (selectedPlaylists.contains(playlist)) {
                                selectedPlaylists.remove(playlist)
                            } else {
                                selectedPlaylists.add(playlist)
                            }

                            listener.onPlaylistsSelected(selectedPlaylists)
                            isSelected = selectedPlaylists.contains(playlist)
                        } else {
                            listener.onPlaylistSelected(playlist)
                        }
                    }
                }
            }
        }

        private fun showMenu(view: View, playlist: Playlist) {
            PopupMenu(view.context, view).let { popup ->
                popup.menuInflater.inflate(R.menu.playlist_menu, popup.menu)
                popup.setForceShowIcon(true)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.renameMenuItem -> listener.onPlaylistRename(playlist)
                        R.id.deleteMenuItem -> listener.onPlaylistDelete(playlist)
                    }
                    true
                }
                popup.show()
            }
        }
    }

    abstract class OnPlaylistSelectedListener {
        open fun onPlaylistsSelected(playlists: List<Playlist>) = Unit
        open fun onPlaylistSelected(playlist: Playlist) = Unit
        open fun onPlaylistRename(playlist: Playlist) = Unit
        open fun onPlaylistDelete(playlist: Playlist) = Unit
    }
}