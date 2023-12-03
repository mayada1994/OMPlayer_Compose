package com.omplayer.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omplayer.app.databinding.ItemTrackBinding
import com.omplayer.app.db.entities.Video

class BookmarkedVideosAdapter(
    private val items: List<Video>,
    private val listener: OnVideoSelectedListener
) : RecyclerView.Adapter<BookmarkedVideosAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder =
        VideoViewHolder(ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VideoViewHolder(private val viewBinding: ItemTrackBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(video: Video) {
            with(viewBinding) {
                txtTitle.text = video.title
                txtArtist.text = video.artist
                root.setOnClickListener { listener.onVideoSelected(video) }
            }
        }
    }

    interface OnVideoSelectedListener {
        fun onVideoSelected(video: Video)
    }
}