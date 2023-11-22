package com.omplayer.app.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.palette.graphics.Palette
import coil.load
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import com.omplayer.app.R
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.databinding.FragmentPlayerBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.extensions.toFormattedTime
import com.omplayer.app.utils.LibraryUtils
import com.omplayer.app.viewmodels.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class PlayerFragment : BaseMvvmFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {

    override val viewModel: PlayerViewModel by viewModels()

    private var mediaController: MediaControllerCompat? = null

    private val callback = object: MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            try {
                when (state?.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        binding.btnPlay.setImageResource(R.drawable.ic_pause_circle)
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        binding.btnPlay.setImageResource(R.drawable.ic_play_circle)
                    }
                    PlaybackStateCompat.STATE_STOPPED -> {
                        binding.btnPlay.setImageResource(R.drawable.ic_play_circle)
                        binding.seekBar.progress = 0
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaController = MediaControllerCompat.getMediaController(requireActivity() as MainActivity)

        mediaController?.let { mediaController ->
            LibraryUtils.currentTrack.distinctUntilChanged().observe(viewLifecycleOwner) {
                (activity as MainActivity).playTrack(it)
                updateUI(it)
            }

            with(binding) {
                LibraryUtils.currentTrackProgress.distinctUntilChanged().observe(viewLifecycleOwner) {
                    seekBar.progress = it.toInt()
                    txtCurrentPosition.text = it.toFormattedTime()
                }

                seekBar.apply {
                    LibraryUtils.currentTrack.value?.let {
                        progress = mediaController.playbackState?.position?.toInt() ?: 0
                        max = it.duration
                        txtDuration.text = it.duration.toLong().toFormattedTime()
                        txtCurrentPosition.text = mediaController.playbackState?.position?.toFormattedTime() ?: "00:00"
                    }
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) = Unit

                        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            seekBar?.let {
                                mediaController.transportControls.seekTo(it.progress.toLong())
                                LibraryUtils.currentTrackProgress.value = it.progress.toLong()
                            }
                        }
                    })
                }
                btnPlay.setOnClickListener {
                    if (mediaController.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                        mediaController.transportControls.pause()
                    } else {
                        mediaController.transportControls.play()
                    }
                }
                btnNext.setOnClickListener { viewModel.skipTrack { mediaController.transportControls.skipToNext() } }
                btnPrev.setOnClickListener { viewModel.skipTrack { mediaController.transportControls.skipToPrevious() } }
                btnPlaybackMode.setOnClickListener { viewModel.changePlaybackMode() }
                btnAddToPlaylist.setOnClickListener { viewModel.onAddToPlaylistsClick() }

                btnMenu.setOnClickListener { showMenu(btnMenu) }

                btnBack.setOnClickListener { viewModel.onBackPressed() }
            }

            viewModel.getInitialPlaybackModeIcon()

            mediaController.registerCallback(callback)
        }
    }

    private fun updateUI(track: Track) {
        with(binding) {
            txtTitle.text = track.title
            txtArtist.text = track.artist

            imgCover.load(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    LibraryUtils.getAlbumCover(requireContext(), track.id)
                } else {
                    LibraryUtils.getAlbumCover(track.id)
                }
            ) {
                listener(object : ImageRequest.Listener {
                    override fun onError(request: ImageRequest, result: ErrorResult) {
                        val color = ContextCompat.getColor(requireContext(), R.color.black_90)
                        val colorWithAlpha = Color.argb((Color.alpha(color) * 0.6).roundToInt(), Color.red(color), Color.green(color), Color.blue(color))
                        val drawableBackground = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            colors = intArrayOf(color, colorWithAlpha)
                        }
                        playerContainer.background = drawableBackground
                    }

                    override fun onSuccess(request: ImageRequest, result: SuccessResult) {
                        result.drawable.let {
                            val palette = Palette.from(it.toBitmap()).generate()
                            val color = palette.getDarkMutedColor(ContextCompat.getColor(requireContext(), R.color.black_90))
                            val colorWithAlpha = Color.argb((Color.alpha(color) * 0.6).roundToInt(), Color.red(color), Color.green(color), Color.blue(color))
                            val drawableBackground = GradientDrawable().apply {
                                shape = GradientDrawable.RECTANGLE
                                colors = intArrayOf(color, colorWithAlpha)
                            }
                            playerContainer.background = drawableBackground
                        }
                    }
                })
                crossfade(true)
                transformations(CircleCropTransformation())
                diskCachePolicy(CachePolicy.DISABLED)
                memoryCachePolicy(CachePolicy.DISABLED)
                placeholder(R.drawable.ic_cover_placeholder)
                error(R.drawable.ic_cover_placeholder)
            }

            seekBar.progress = mediaController?.playbackState?.position?.toInt() ?: 0
            seekBar.max = track.duration
            txtCurrentPosition.text = mediaController?.playbackState?.position?.toFormattedTime() ?: "00:00"
            txtDuration.text = track.duration.toLong().toFormattedTime()
        }
    }

    private fun showMenu(view: View) {
        PopupMenu(requireContext(), view).let { popup ->
            popup.menuInflater.inflate(R.menu.player_menu, popup.menu)
            if (!viewModel.isScrobblingEnabled) {
                popup.menu.removeItem(R.id.loveMenuItem)
            }
            popup.setForceShowIcon(true)
            popup.setOnMenuItemClickListener { menuItem ->
                viewModel.onMenuItemClicked(menuItem.itemId, requireContext())
                true
            }
            popup.show()
        }
    }

    override fun handleCustomEvent(event: ViewEvent): Boolean {
        return when (event) {
            is PlayerViewModel.CustomEvent.UpdatePlaybackModeIcon -> {
                binding.btnPlaybackMode.setImageResource(event.iconRes)
                true
            }

            else -> super.handleCustomEvent(event)
        }
    }

    override fun onDestroy() {
        mediaController?.unregisterCallback(callback)
        super.onDestroy()
    }

}