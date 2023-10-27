package com.omplayer.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omplayer.app.databinding.ItemSimilarTrackBinding
import com.omplayer.app.network.responses.LastFmSimilarTracksResponse.LastFmSimilarTrack

class SimilarTracksAdapter(
    private val items: List<LastFmSimilarTrack>,
    private val listener: OnSimilarTrackSelectedListener
) : RecyclerView.Adapter<SimilarTracksAdapter.SimilarTrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarTrackViewHolder =
        SimilarTrackViewHolder(
            ItemSimilarTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: SimilarTrackViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SimilarTrackViewHolder(private val viewBinding: ItemSimilarTrackBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(track: LastFmSimilarTrack) {
            with(viewBinding) {
                txtTitle.text = track.name
                txtArtist.text = track.artist.name
                btnStar.setOnClickListener { listener.onTrackStarred(track) }
                root.setOnClickListener { listener.onTrackSelected(track) }
            }
        }
    }

    interface OnSimilarTrackSelectedListener {
        fun onTrackSelected(track: LastFmSimilarTrack)
        fun onTrackStarred(track: LastFmSimilarTrack)
    }
}