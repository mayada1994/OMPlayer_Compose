package com.omplayer.app.callbacks

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import com.omplayer.app.entities.Track
import com.omplayer.app.services.MediaPlaybackService

class PlayerMediaSessionCallback(
    private val context: Context,
    private val mediaSession: MediaSessionCompat,
    private val listener: OnMediaSessionStoppedListener
) : MediaSessionCompat.Callback() {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)
        val track: Track? = extras?.getParcelable(TRACK_EXTRA)
        track?.let { setNewTrack(it, uri) }
        setMediaPlaybackState()
    }

    override fun onPlay() {
        super.onPlay()
        mediaPlayer.start()
        setMediaPlaybackState(position = mediaPlayer.currentPosition.toLong())
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
        setMediaPlaybackState(state = PlaybackStateCompat.STATE_PAUSED, mediaPlayer.currentPosition.toLong())
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        setMediaPlaybackState(state = PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        setMediaPlaybackState(state = PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        mediaPlayer.seekTo(pos.toInt())
        setMediaPlaybackState(
            state = if (mediaPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
            position = pos
        )
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        setMediaPlaybackState(state = PlaybackStateCompat.STATE_STOPPED)
        listener.onStop()
    }

    private fun setMediaPlaybackState(
        state: Int = PlaybackStateCompat.STATE_PLAYING,
        position: Long = 0
    ) {
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_STOP
            )
            .setState(state, position, PLAYBACK_SPEED)
        mediaSession.setPlaybackState(stateBuilder.build())
        if (state != PlaybackStateCompat.STATE_STOPPED) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, MediaPlaybackService::class.java)
            )
        }
    }

    private fun setNewTrack(track: Track, uri: Uri?) {
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    uri.toString()
                )
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
                .build()
        )

        mediaPlayer.apply {
            reset()
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(track.path)
            prepareAsync()
            setOnPreparedListener {
                mediaSession.controller.transportControls.play()
            }
            setOnCompletionListener {
                setMediaPlaybackState(state = PlaybackStateCompat.STATE_PAUSED)
            }
        }
    }

    interface OnMediaSessionStoppedListener {
        fun onStop()
    }

    companion object {
        private const val PLAYBACK_SPEED = 1.0f
        const val TRACK_EXTRA = "track"
    }
}