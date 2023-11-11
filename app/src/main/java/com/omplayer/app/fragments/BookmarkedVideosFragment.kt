package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.DividerItemDecoration
import com.omplayer.app.R
import com.omplayer.app.adapters.BookmarkedVideosAdapter
import com.omplayer.app.databinding.FragmentBookmarkedVideosBinding
import com.omplayer.app.db.entities.Video
import com.omplayer.app.viewmodels.BookmarkedVideosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkedVideosFragment : BaseMvvmFragment<FragmentBookmarkedVideosBinding>(FragmentBookmarkedVideosBinding::inflate) {

    override val viewModel: BookmarkedVideosViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getBookmarkedVideos()

        viewModel.bookmarkedVideos.distinctUntilChanged().observe(viewLifecycleOwner) { videos ->
            with(binding) {
                if (videos.isNullOrEmpty()) {
                    txtPlaceholder.isVisible = true
                    rvBookmarkedVideos.isVisible = false
                } else {
                    txtPlaceholder.isVisible = false
                    rvBookmarkedVideos.isVisible = true

                    rvBookmarkedVideos.apply {
                        adapter = BookmarkedVideosAdapter(videos, object : BookmarkedVideosAdapter.OnVideoSelectedListener {
                            override fun onVideoSelected(video: Video) {
                                viewModel.onVideoSelected(video)
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
}