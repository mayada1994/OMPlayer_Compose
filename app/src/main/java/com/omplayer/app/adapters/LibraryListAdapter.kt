package com.omplayer.app.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.omplayer.app.R
import com.omplayer.app.databinding.ItemAlbumBinding
import com.omplayer.app.databinding.ItemArtistBinding
import com.omplayer.app.databinding.ItemGenreBinding
import com.omplayer.app.databinding.ItemTrackBinding
import com.omplayer.app.entities.Album
import com.omplayer.app.entities.Artist
import com.omplayer.app.entities.Genre
import com.omplayer.app.entities.Track
import com.omplayer.app.utils.LibraryUtils

class LibraryListAdapter(
    private val items: List<Any>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ItemViewType(@LayoutRes val resId: Int) {
        TRACK(R.layout.item_track),
        ARTIST(R.layout.item_artist),
        ALBUM(R.layout.item_album),
        GENRE(R.layout.item_genre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemViewType.ARTIST.resId -> ArtistViewHolder(ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ItemViewType.ALBUM.resId -> AlbumViewHolder(ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ItemViewType.GENRE.resId -> GenreViewHolder(ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> TrackViewHolder(ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrackViewHolder -> holder.onBind(items[position] as Track)
            is ArtistViewHolder -> holder.onBind(items[position] as Artist)
            is AlbumViewHolder -> holder.onBind(items[position] as Album)
            is GenreViewHolder -> holder.onBind(items[position] as Genre)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Artist -> ItemViewType.ARTIST.resId
        is Album -> ItemViewType.ALBUM.resId
        is Genre -> ItemViewType.GENRE.resId
        else -> ItemViewType.TRACK.resId
    }

    inner class TrackViewHolder(private val viewBinding: ItemTrackBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(track: Track) {
            with(viewBinding) {
                txtTitle.text = track.title
                txtArtist.text = track.artist
                root.setOnClickListener { listener.onItemClick(track) }
            }
        }
    }

    inner class ArtistViewHolder(private val viewBinding: ItemArtistBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(artist: Artist) {
            with(viewBinding) {
                txtName.text = artist.name
                root.setOnClickListener { listener.onItemClick(artist) }
            }
        }
    }

    inner class AlbumViewHolder(private val viewBinding: ItemAlbumBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(album: Album) {
            with(viewBinding) {
                txtTitle.text = album.title
                txtYear.text = album.year
                Glide.with(root.context)
                    .load(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            LibraryUtils.getAlbumCover(root.context, album.cover)
                        } else {
                            album.cover
                        }
                    )
                    .transform(CircleCrop())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_cover_placeholder)
                    .into(imgCover)
                root.setOnClickListener { listener.onItemClick(album) }
            }
        }
    }

    inner class GenreViewHolder(private val viewBinding: ItemGenreBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun onBind(genre: Genre) {
            with(viewBinding) {
                txtTitle.text = genre.title
                root.setOnClickListener { listener.onItemClick(genre) }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Any)
    }
}