package com.omplayer.app.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.omplayer.app.R
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.databinding.FragmentVideoBinding
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.viewmodels.VideoViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VideoFragment : BaseMvvmFragment<FragmentVideoBinding>(FragmentVideoBinding::inflate) {

    override val viewModel: VideoViewModel by viewModels()

    private val args: VideoFragmentArgs by navArgs()

    private var isFullscreen = false

    private var currentVideoDuration: Float = 0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            lifecycle.addObserver(youtubeVideoView)

            txtArtist.text = args.artist
            txtTitle.text = args.title

            btnBack.setOnClickListener { viewModel.onBackPressed() }

            youtubeVideoView.addFullscreenListener(object : FullscreenListener {
                override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                    isFullscreen = true

                    youtubeVideoView.visibility = View.GONE
                    fullscreenViewContainer.visibility = View.VISIBLE
                    fullscreenViewContainer.addView(fullscreenView)

                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

                override fun onExitFullscreen() {
                    isFullscreen = false

                    youtubeVideoView.visibility = View.VISIBLE
                    fullscreenViewContainer.visibility = View.GONE
                    fullscreenViewContainer.removeAllViews()

                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }

            })

            viewModel.getVideo(
                artist = args.artist,
                title = args.title
            )
        }
    }

    override fun handleCustomEvent(event: ViewEvent): Boolean {
        return when(event) {
            is VideoViewModel.CustomEvent.PlayVideo -> {
                with(binding) {
                    txtPlaceholder.isVisible = false
                    youtubeVideoView.isVisible = true
                }
                playVideo(event.videoId)
                true
            }

            is VideoViewModel.CustomEvent.ShowPlaceholder -> {
                with(binding) {
                    txtPlaceholder.isVisible = true
                    youtubeVideoView.isVisible = false
                }
                true
            }

            is VideoViewModel.CustomEvent.UpdateBookmarkState -> {
                with(binding.btnStar) {
                    visibility = if (args.isSimilarTrack && !viewModel.isLocalTrack()) View.VISIBLE else View.INVISIBLE
                    setImageResource(
                        if (event.isStarred) R.drawable.ic_star else R.drawable.ic_star_border
                    )
                }
                true
            }

            else -> super.handleCustomEvent(event)
        }
    }

    private fun playVideo(videoId: String) {
        with(binding) {
            val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    requireActivity().onBackPressedDispatcher.addCallback(this@VideoFragment) {
                        if (isFullscreen) {
                            youTubePlayer.toggleFullscreen()
                        } else {
                            viewModel.onBackPressed()
                        }
                    }

                    youTubePlayer.loadVideo(videoId, 0f)

                    viewModel.onPlaybackStarted(requireContext())
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    if (state == PlayerConstants.PlayerState.BUFFERING
                        && (activity as MainActivity?)?.mediaController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                        viewModel.pauseCurrentTrack()
                    }
                }

                override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                    currentVideoDuration = duration
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    viewModel.handlePlaybackProgress(second, currentVideoDuration, requireContext())
                }
            }

            try {
                // we need to initialize manually in order to pass IFramePlayerOptions to the player
                youtubeVideoView.enableAutomaticInitialization = false

                val iFramePlayerOptions = IFramePlayerOptions.Builder()
                    .controls(1)
                    .fullscreen(1)
                    .rel(0)
                    .ivLoadPolicy(3)
                    .ccLoadPolicy(0)
                    .build()

                youtubeVideoView.initialize(youTubePlayerListener, iFramePlayerOptions)
            } catch (e: Exception) {
                youtubeVideoView.enableAutomaticInitialization = true

                youtubeVideoView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.addListener(youTubePlayerListener)

                        youTubePlayer.loadVideo(videoId, 0f)
                        viewModel.onPlaybackStarted(requireContext())
                    }
                })
            }

            btnStar.setOnClickListener { viewModel.changeBookmarkState() }
        }
    }
}