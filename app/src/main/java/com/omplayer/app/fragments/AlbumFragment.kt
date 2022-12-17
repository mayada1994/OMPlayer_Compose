package com.omplayer.app.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.omplayer.app.R
import com.omplayer.app.adapters.TracklistAdapter
import com.omplayer.app.databinding.FragmentAlbumBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.utils.LibraryUtils
import com.omplayer.app.viewmodels.AlbumViewModel

class AlbumFragment : BaseMvvmFragment<FragmentAlbumBinding>(FragmentAlbumBinding::inflate) {

    override val viewModel: AlbumViewModel by viewModels()

    private val args: AlbumFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(args.album)

        with(binding) {
            args.album?.let {
                txtTitle.text = it.title
                txtArtist.text = it.artist
                txtYear.text = it.year
                Glide.with(this@AlbumFragment)
                    .load(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            LibraryUtils.getAlbumCover(requireContext(), it.cover)
                        } else {
                            it.cover
                        }
                    )
                    .placeholder(R.drawable.placeholder)
                    .into(imgCover)
            }
            viewModel.tracklist.observe(viewLifecycleOwner) {
                rvTracks.apply {
                    adapter = TracklistAdapter(it, object : TracklistAdapter.OnTrackSelectedListener{
                        override fun onTrackSelected(track: Track) {
                            viewModel.onTrackSelected(track)
                        }
                    })

                    addItemDecoration(
                        DividerItemDecoration(
                            this.context,
                            DividerItemDecoration.VERTICAL
                        ).apply {
                            ContextCompat.getDrawable(context, R.drawable.line_divider)?.let { setDrawable(it) }
                        }
                    )
                }
            }
        }
    }

}