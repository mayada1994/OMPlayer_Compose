package com.omplayer.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omplayer.app.R
import com.omplayer.app.databinding.ItemAlbumTrackBinding
import com.omplayer.app.db.entities.Track
import com.omplayer.app.extensions.toFormattedTime
import java.util.Collections.max

class AlbumTracksAdapter(
    private val items: List<Track>,
    private val listener: OnTrackSelectedListener
) : RecyclerView.Adapter<AlbumTracksAdapter.TracklistViewHolder>() {

    private val padStartLength = max(items.map { it.position }).toString().length.takeIf { it > 1 } ?: 2
    private val padEndLength = max(items.map { it.duration.toLong().toFormattedTime() }).length

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracklistViewHolder =
        TracklistViewHolder(ItemAlbumTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: TracklistViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TracklistViewHolder(private val viewBinding: ItemAlbumTrackBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(track: Track) {
            with(viewBinding) {
                txtPosition.text = root.context.getString(R.string.track_position, track.position.toString().padStart(padStartLength, '0'))
                txtDuration.text = track.duration.toLong().toFormattedTime().padEnd(padEndLength, ' ')
                txtTitle.text = track.title
                root.setOnClickListener { listener.onTrackSelected(track) }
            }
        }
    }

    interface OnTrackSelectedListener {
        fun onTrackSelected(track: Track)
    }
}