package com.omplayer.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omplayer.app.databinding.ItemTrackBinding
import com.omplayer.app.db.entities.Track

class PlaylistTracksAdapter(
    private val items: List<Track>,
    private val listener: OnTracksSelectedListener
) : RecyclerView.Adapter<PlaylistTracksAdapter.TracklistViewHolder>() {

    private val selectedTracks: ArrayList<Int> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracklistViewHolder =
        TracklistViewHolder(ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TracklistViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TracklistViewHolder(private val viewBinding: ItemTrackBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(track: Track) {
            with(viewBinding) {
                txtTitle.text = track.title
                txtArtist.text = track.artist
                root.setOnClickListener {
                    if (selectedTracks.contains(track.id)) {
                        it.isSelected = false
                        selectedTracks.remove(track.id)
                    } else {
                        it.isSelected = true
                        selectedTracks.add(track.id)
                    }

                    listener.onTracksSelected(selectedTracks)
                }
            }
        }
    }

    interface OnTracksSelectedListener {
        fun onTracksSelected(selectedTracks: List<Int>)
    }
}