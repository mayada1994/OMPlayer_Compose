package com.omplayer.app.fragments

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.omplayer.app.R
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.databinding.FragmentPlayerBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.utils.LibraryUtils
import com.omplayer.app.viewmodels.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerFragment : BaseMvvmFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {

    override val viewModel: PlayerViewModel by viewModels()

    private val args: PlayerFragmentArgs by navArgs()

    private var currentTrack: Track? = null

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
                PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                    currentTrack = (activity as MainActivity).playNextTrack(currentTrack!!)
                    binding.txtTitle.text = currentTrack!!.title
                    binding.txtArtist.text = currentTrack!!.artist
                    Glide.with(this@PlayerFragment)
                        .load(LibraryUtils.getAlbumCover(requireContext(), currentTrack!!.id))
                        .placeholder(R.drawable.placeholder)
                        .into(binding.imgCover)
                    binding.seekBar.progress = 0
                    binding.seekBar.max = currentTrack!!.duration
                }
                PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> {
                    currentTrack = (activity as MainActivity).playPreviousTrack(currentTrack!!)
                    binding.txtTitle.text = currentTrack!!.title
                    binding.txtArtist.text = currentTrack!!.artist
                    Glide.with(this@PlayerFragment)
                        .load(LibraryUtils.getAlbumCover(requireContext(), currentTrack!!.id))
                        .placeholder(R.drawable.placeholder)
                        .into(binding.imgCover)
                    binding.seekBar.progress = 0
                    binding.seekBar.max = currentTrack!!.duration
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaController = MediaControllerCompat.getMediaController(requireActivity() as MainActivity)

        mediaController?.let { mediaController ->
            binding.btnPlay.setOnClickListener {
                if (mediaController.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                } else {
                    mediaController.transportControls.play()
                }
            }

            args.track?.let {
                currentTrack = args.track
                currentTrack?.let { (activity as MainActivity).playTrack(it) }

                with(binding) {
                    txtTitle.text = it.title
                    txtArtist.text = it.artist
                    Glide.with(this@PlayerFragment)
                        .load(LibraryUtils.getAlbumCover(requireContext(), it.id))
                        .placeholder(R.drawable.placeholder)
                        .into(imgCover)

                    seekBar.apply {
                        progress = 0
                        max = it.duration
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
                    btnNext.setOnClickListener { mediaController.transportControls.skipToNext() }
                    btnPrev.setOnClickListener { mediaController.transportControls.skipToPrevious() }
                }
            }

            mediaController.registerCallback(callback)
        }
    }

    private fun trackProgress(mediaController: MediaControllerCompat) {
        lifecycleScope.launch {
            while (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                delay(500)
                binding.seekBar.progress = mediaController.playbackState.position.toInt()
            }
        }
    }

    override fun onDestroy() {
        mediaController?.unregisterCallback(callback)
        super.onDestroy()
    }

}