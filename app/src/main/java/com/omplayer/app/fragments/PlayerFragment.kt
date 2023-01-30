package com.omplayer.app.fragments

import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.omplayer.app.R
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.databinding.FragmentPlayerBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.extensions.toFormattedTime
import com.omplayer.app.utils.LibraryUtils
import com.omplayer.app.viewmodels.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerFragment : BaseMvvmFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {

    override val viewModel: PlayerViewModel by viewModels()

    private val args: PlayerFragmentArgs by navArgs()

    private var mediaController: MediaControllerCompat? = null

    private val callback = object: MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            when(state?.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    binding.btnPlay.setImageResource(R.drawable.ic_pause_circle)
                    mediaController?.let { trackProgress(it) }
                }
                PlaybackStateCompat.STATE_PAUSED -> {
                    binding.btnPlay.setImageResource(R.drawable.ic_play_circle)
                }
                PlaybackStateCompat.STATE_STOPPED -> {
                    binding.btnPlay.setImageResource(R.drawable.ic_play_circle)
                    binding.seekBar.progress = 0
                }
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

            args.track?.let { LibraryUtils.currentTrack.value = args.track }

            with(binding) {
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
                            seekBar?.let { mediaController.transportControls.seekTo(it.progress.toLong()) }
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
            }

            mediaController.registerCallback(callback)
        }
    }

    private fun updateUI(track: Track) {
        with(binding) {
            txtTitle.text = track.title
            txtArtist.text = track.artist
            Glide.with(this@PlayerFragment)
                .load(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        LibraryUtils.getAlbumCover(requireContext(), track.id)
                    } else {
                        LibraryUtils.getAlbumCover(track.id)
                    }
                )
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.placeholder)
                .into(imgCover)
            seekBar.progress = mediaController?.playbackState?.position?.toInt() ?: 0
            seekBar.max = track.duration
            txtCurrentPosition.text = mediaController?.playbackState?.position?.toFormattedTime() ?: "00:00"
            txtDuration.text = track.duration.toLong().toFormattedTime()
        }
    }

    private fun trackProgress(mediaController: MediaControllerCompat) {
        lifecycleScope.launch {
            while (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                delay(500)
                binding.seekBar.progress = mediaController.playbackState.position.toInt()
                binding.txtCurrentPosition.text = mediaController.playbackState.position.toFormattedTime()
            }
        }
    }

    override fun onDestroy() {
        mediaController?.unregisterCallback(callback)
        super.onDestroy()
    }

}