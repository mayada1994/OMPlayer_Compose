package com.omplayer.app.fragments

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.omplayer.app.R
import com.omplayer.app.databinding.FragmentPlayerBinding
import com.omplayer.app.viewmodels.PlayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerFragment : BaseFragment<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {

    override val viewModel: PlayerViewModel by viewModels()

    private val args: PlayerFragmentArgs by navArgs()

    private val mediaPlayer = MediaPlayer()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.track?.let {
            with(binding) {
                txtTitle.text = it.title
                txtArtist.text = it.artist

                mediaPlayer.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(it.path)
                    prepare()
                    setOnPreparedListener {
                        btnPlay.setImageResource(R.drawable.ic_pause_circle)
                        start()
                        trackProgress()
                    }
                    setOnCompletionListener {
                        btnPlay.setImageResource(R.drawable.ic_play_circle)
                        seekBar.progress = 0
                    }
                }

                btnPlay.setOnClickListener {
                    if (mediaPlayer.isPlaying) {
                        btnPlay.setImageResource(R.drawable.ic_play_circle)
                        mediaPlayer.pause()
                    } else {
                        btnPlay.setImageResource(R.drawable.ic_pause_circle)
                        mediaPlayer.start()
                        trackProgress()
                    }
                }
                seekBar.apply {
                    progress = 0
                    max = mediaPlayer.duration
                    setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) = Unit

                        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            seekBar?.let { mediaPlayer.seekTo(it.progress) }
                            trackProgress()
                        }

                    })
                }
            }
        }
    }

    private fun trackProgress() {
        lifecycleScope.launch {
            while (mediaPlayer.isPlaying && mediaPlayer.currentPosition < mediaPlayer.duration) {
                delay(100)
                binding.seekBar.progress = mediaPlayer.currentPosition
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}