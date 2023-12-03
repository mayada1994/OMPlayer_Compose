package com.omplayer.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omplayer.app.databinding.ItemTrackBinding
import com.omplayer.app.db.entities.Track

class TracklistAdapter(
    private val items: List<Track>,
    private val listener: OnTrackSelectedListener
) : RecyclerView.Adapter<TracklistAdapter.TracklistViewHolder>() {

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
                root.setOnClickListener { listener.onTrackSelected(track) }
            }
        }
    }

    interface OnTrackSelectedListener {
        fun onTrackSelected(track: Track)
    }
}