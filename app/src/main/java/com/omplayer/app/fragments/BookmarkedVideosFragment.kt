package com.omplayer.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.omplayer.app.R
import com.omplayer.app.databinding.FragmentBookmarkedVideosBinding
import com.omplayer.app.db.entities.Video
import com.omplayer.app.items.TrackItem
import com.omplayer.app.theme.OMPlayerTheme
import com.omplayer.app.viewmodels.BookmarkedVideosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkedVideosFragment : BaseMvvmFragment<FragmentBookmarkedVideosBinding>(FragmentBookmarkedVideosBinding::inflate) {

    override val viewModel: BookmarkedVideosViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            binding.composeView.apply {
                // Dispose the Composition when the view's LifecycleOwner is destroyed
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )

                setContent {
                    OMPlayerTheme {
                        BookmarkedVideosScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getBookmarkedVideos()
    }

    @Composable
    fun BookmarkedVideosScreen(viewModel: BookmarkedVideosViewModel) {
        val videos by viewModel.bookmarkedVideos.observeAsState()

        BookmarkedVideosUI(
            videos = videos ?: listOf(),
            onVideoSelected = { viewModel.onVideoSelected(it) },
            onBackPressed = { viewModel.onBackPressed() })
    }

    @Composable
    fun BookmarkedVideosUI(
        videos: List<Video>,
        onVideoSelected: (Video) -> Unit,
        onBackPressed: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorResource(id = R.color.colorBackground)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                IconButton(
                    onClick = { onBackPressed() },
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = null
                    )
                }

                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)) {
                    if (videos.isNotEmpty()) {
                        LazyColumn(content = {
                            items(videos) { video ->
                                TrackItem(title = video.title, artist = video.artist, modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = 8.dp, vertical = 12.dp)
                                    .clickable {
                                        onVideoSelected(video)
                                    })

                                if (videos.indexOf(video) != videos.lastIndex) {
                                    Divider(
                                        color = colorResource(id = R.color.colorDivider),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        })
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .align(Alignment.Center),
                            text = stringResource(id = R.string.no_bookmarked_videos),
                            color = colorResource(id = R.color.colorTextSecondary),
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewBookmarkedVideosScreen() {
        OMPlayerTheme {
            BookmarkedVideosUI(
                videos = listOf(
                    Video("videoId", "Powerwolf", "Army of the Night"),
                    Video("videoId", "Nightwish", "Nemo"),
                    Video("videoId", "Kamelot", "Abandoned"),
                    Video("videoId", "Delain", "Stardust"),
                ),
                onVideoSelected = {},
                onBackPressed = {}
            )
        }
    }
}