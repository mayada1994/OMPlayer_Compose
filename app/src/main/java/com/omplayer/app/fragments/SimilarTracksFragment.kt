package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.SimilarTracksAdapter
import com.omplayer.app.adapters.SimilarTracksAdapter.OnSimilarTrackSelectedListener
import com.omplayer.app.databinding.FragmentSimilarTracksBinding
import com.omplayer.app.network.responses.LastFmSimilarTracksResponse.LastFmSimilarTrack
import com.omplayer.app.viewmodels.SimilarTracksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimilarTracksFragment : BaseMvvmFragment<FragmentSimilarTracksBinding>(FragmentSimilarTracksBinding::inflate) {

    override val viewModel: SimilarTracksViewModel by viewModels()

    private val args: SimilarTracksFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSimilarTracks(args.track, requireContext())

        with(binding) {
            args.track.let {
                txtTitle.text = requireContext().getString(R.string.tracks_similar_to, it.artist, it.title)
            }

            viewModel.similarTracks.observe(viewLifecycleOwner) { list ->
                txtPlaceholder.isVisible = list.isNullOrEmpty()

                if (!list.isNullOrEmpty()) {
                    rvSimilarTracks.apply {
                        adapter =
                            SimilarTracksAdapter(list, object : OnSimilarTrackSelectedListener {
                                override fun onTrackSelected(track: LastFmSimilarTrack) {
                                    viewModel.onTrackSelected(track)
                                }
                            })

                        addItemDecoration(
                            DividerItemDecoration(
                                context,
                                DividerItemDecoration.VERTICAL
                            ).apply {
                                ContextCompat.getDrawable(context, R.drawable.line_divider)?.let { setDrawable(it) }
                            }
                        )
                    }
                }
            }

            btnBack.setOnClickListener { viewModel.onBackPressed() }
        }
    }
}