package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.LibraryListAdapter
import com.omplayer.app.databinding.FragmentArtistBinding
import com.omplayer.app.entities.Album
import com.omplayer.app.viewmodels.ArtistViewModel


class ArtistFragment : BaseMvvmFragment<FragmentArtistBinding>(FragmentArtistBinding::inflate) {

    override val viewModel: ArtistViewModel by viewModels()

    private val args: ArtistFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(args.artist)

        binding.txtArtist.text = args.artist?.name

        viewModel.albums.observe(viewLifecycleOwner) {
            binding.rvAlbums.apply {
                adapter = LibraryListAdapter(it, object : LibraryListAdapter.OnItemClickListener {
                    override fun onItemClick(item: Any) {
                        viewModel.onAlbumSelected(item as Album)
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