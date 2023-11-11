package com.omplayer.app.callbacks

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.omplayer.app.R
import com.omplayer.app.entities.Track
import com.omplayer.app.enums.ScrobbleMediaType
import com.omplayer.app.repositories.LastFmRepository
import com.omplayer.app.services.MediaPlaybackService
import com.omplayer.app.utils.LibraryUtils
import com.omplayer.app.workers.LastFmTrackScrobbleWorker
import com.omplayer.app.workers.LastFmTrackUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class PlayerMediaSessionCallback(
    private val context: Context,
    private val mediaSession: MediaSessionCompat,
    private val listener: OnMediaSessionStoppedListener
) : MediaSessionCompat.Callback() {

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    private var progressTracker: CountDownTimer? = null

    private var lastPlayedTrackUri: Uri? = null

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)

        if (uri != null && lastPlayedTrackUri == uri) {
            setMediaPlaybackState(
                state = if (mediaPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                position = mediaPlayer.currentPosition.toLong()
            )
            return
        }

        val track: Track? = extras?.getParcelable(TRACK_EXTRA)
        track?.let { setNewTrack(it, uri) }
        setMediaPlaybackState()
    }

    override fun onPlay() {
        super.onPlay()
        mediaPlayer.start()
        setMediaPlaybackState(position = mediaPlayer.currentPosition.toLong())
        trackProgress()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
        setMediaPlaybackState(state = PlaybackStateCompat.STATE_PAUSED, mediaPlayer.currentPosition.toLong())
        progressTracker?.cancel()
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        setMediaPlaybackState(state = PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
        LibraryUtils.playNextTrack()
        LibraryUtils.currentTrack.value?.let {
            setNewTrack(it, it.path.toUri())
        }
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        setMediaPlaybackState(state = PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS)
        LibraryUtils.playPreviousTrack()
        LibraryUtils.currentTrack.value?.let {
            setNewTrack(it, it.path.toUri())
        }
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
        LibraryUtils.currentTrackProgress.value = 0
        listener.onStop()
        progressTracker?.cancel()
        progressTracker = null
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
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.IO) {
                mediaSession.setMetadata(
                    MediaMetadataCompat.Builder()
                        .putString(
                            MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                            uri.toString()
                        )
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
                        .putBitmap(
                            MediaMetadataCompat.METADATA_KEY_ART,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                LibraryUtils.getAlbumCover(context, track.id)
                            } else {
                                try {
                                    Glide.with(context)
                                        .asBitmap()
                                        .load(LibraryUtils.getAlbumCover(track.id))
                                        .placeholder(R.drawable.ic_cover_placeholder)
                                        .error(R.drawable.ic_cover_placeholder)
                                        .submit().get()
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        )
                        .build()
                )
            }
        }

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
                lastPlayedTrackUri = uri
                LibraryUtils.wasCurrentTrackScrobbled = false
                trackProgress()
            }
            setOnCompletionListener {
                if (!LibraryUtils.isSingleTrackPlaylist()) {
                    setMediaPlaybackState(state = PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
                    mediaSession.controller.transportControls.skipToNext()
                } else {
                    setNewTrack(track, uri)
                }
            }
        }
    }

    private fun trackProgress() {
        progressTracker = object : CountDownTimer(
            (mediaPlayer.duration - mediaPlayer.currentPosition).toLong(),
            500L
        ) {
            override fun onTick(millisUntilFinished: Long) {
                // Do not remove to prevent track skipping bug when previous track duration is bigger than the current
                if (mediaPlayer.isPlaying) {
                    LibraryUtils.currentTrackProgress.postValue(mediaPlayer.currentPosition.toLong())

                    if (shouldUpdateTrack(mediaPlayer.currentPosition)) {
                        WorkManager.getInstance(context).beginUniqueWork(
                            LastFmTrackUpdateWorker::class.java.simpleName,
                            ExistingWorkPolicy.REPLACE,
                            OneTimeWorkRequestBuilder<LastFmTrackUpdateWorker>().setConstraints(
                                Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()
                            ).build()
                        ).enqueue()
                    }

                    if (shouldScrobbleTrack(mediaPlayer.currentPosition, mediaPlayer.duration)) {
                        WorkManager.getInstance(context).beginUniqueWork(
                            LastFmTrackScrobbleWorker::class.java.simpleName,
                            ExistingWorkPolicy.APPEND_OR_REPLACE,
                            OneTimeWorkRequestBuilder<LastFmTrackScrobbleWorker>()
                                .setBackoffCriteria(
                                    BackoffPolicy.LINEAR,
                                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                    TimeUnit.MILLISECONDS
                                ).build()
                        ).enqueue()
                    }
                }
            }

            override fun onFinish() = Unit
        }
        progressTracker?.start()
    }

    private fun shouldUpdateTrack(currentPosition: Int) =
        System.currentTimeMillis() - LibraryUtils.lastTrackUpdateOnLastFmTime >= LastFmRepository.LAST_FM_TRACK_UPDATE_INTERVAL
                || currentPosition <= 500
                || LibraryUtils.lastUpdatedMediaType != ScrobbleMediaType.TRACK

    private fun shouldScrobbleTrack(currentPosition: Int, duration: Int) =
        !LibraryUtils.wasCurrentTrackScrobbled
                && duration >= LastFmRepository.LAST_FM_MIN_TRACK_DURATION
                && (currentPosition >= LastFmRepository.LAST_FM_MAX_PLAYBACK_DURATION_BEFORE_SCROBBLE
                || currentPosition.toFloat() / duration.toFloat() >= LastFmRepository.LAST_FM_SCROBBLING_PERCENTAGE)

    interface OnMediaSessionStoppedListener {
        fun onStop()
    }

    companion object {
        private const val PLAYBACK_SPEED = 1.0f
        const val TRACK_EXTRA = "track"
    }
}