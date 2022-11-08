package com.omplayer.app.fragments

import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.omplayer.app.R
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.databinding.FragmentPlayerBinding
import com.omplayer.app.viewmodels.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerFragment : BaseFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {

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
            binding.btnPlay.setOnClickListener {
                if (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                } else {
                    mediaController.transportControls.play()
                }
            }

            args.track?.let {
                with(binding) {
                    txtTitle.text = it.title
                    txtArtist.text = it.artist
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
                }
            }

            mediaController.registerCallback(callback)
        }
    }

    private fun trackProgress(mediaController: MediaControllerCompat) {
        lifecycleScope.launch {
            while (mediaController.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                delay(100)
                binding.seekBar.progress = mediaController.playbackState.position.toInt()
            }
        }
    }

    override fun onDestroy() {
        mediaController?.unregisterCallback(callback)
        super.onDestroy()
    }

}