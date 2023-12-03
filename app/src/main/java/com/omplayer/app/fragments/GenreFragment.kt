package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.TracklistAdapter
import com.omplayer.app.databinding.FragmentGenreBinding
import com.omplayer.app.db.entities.Track
import com.omplayer.app.viewmodels.GenreViewModel

class GenreFragment : BaseMvvmFragment<FragmentGenreBinding>(FragmentGenreBinding::inflate) {

    override val viewModel: GenreViewModel by viewModels()

    private val args: GenreFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(args.genre)

        with(binding) {
            txtGenre.text = args.genre?.title

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

            btnBack.setOnClickListener { viewModel.onBackPressed() }
        }

    }
}