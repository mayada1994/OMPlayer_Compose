package com.omplayer.app.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.omplayer.app.R
import com.omplayer.app.entities.Track
import com.omplayer.app.utils.NotificationUtils


class MediaPlaybackService: MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    private var isServiceStarted = false

    private val mediaPlayer = MediaPlayer()

    override fun onCreate() {
        super.onCreate()

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(this, TAG).apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Set the session's token so that client activities can communicate with it
            setSessionToken(this.sessionToken)

            isActive = true

            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
                    super.onPlayFromUri(uri, extras)
                    val track: Track? = extras?.getParcelable(TRACK_EXTRA)
                    setMetadata(
                        MediaMetadataCompat.Builder()
                            .putString(
                                MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                                uri.toString()
                            )
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track?.title)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track?.artist)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track?.album)
                            .build()
                    )
                    setMediaPlaybackState()

                    if (!isServiceStarted) {
                        ContextCompat.startForegroundService(
                            this@MediaPlaybackService,
                            Intent(this@MediaPlaybackService, MediaPlaybackService::class.java).apply {
                                putExtra(TRACK_EXTRA, track)
                            }
                        )
                        startForeground(
                            NOTIFICATION_ID,
                            NotificationUtils.showNotification(
                                this@MediaPlaybackService,
                                this@MediaPlaybackService.mediaSession
                            ).build()
                        )
                        isServiceStarted = true
                    } else {
                        track?.let { setNewTrack(it) }
                    }
                }
                override fun onPlay() {
                    super.onPlay()
                    mediaPlayer.start()
                    setMediaPlaybackState(position = mediaPlayer.currentPosition.toLong())
                }
                override fun onStop() {
                    super.onStop()
                    mediaPlayer.stop()
                    setMediaPlaybackState(state = PlaybackStateCompat.STATE_STOPPED)
                }
                override fun onPause() {
                    super.onPause()
                    mediaPlayer.pause()
                    setMediaPlaybackState(state = PlaybackStateCompat.STATE_PAUSED, mediaPlayer.currentPosition.toLong())
                }

                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer.seekTo(pos.toInt())
                    setMediaPlaybackState(
                        state = if (mediaPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                        position = pos
                    )
                }
            })
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot = BrowserRoot(getString(R.string.app_name), null)

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.getParcelable<Track>(TRACK_EXTRA)?.let {
            setNewTrack(it)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setNewTrack(track: Track) {
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
                setMediaPlaybackState(state = PlaybackStateCompat.STATE_STOPPED)
            }
        }
    }

    private fun setMediaPlaybackState(state: Int = PlaybackStateCompat.STATE_PLAYING, position: Long = 0) {
        stateBuilder = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_STOP
            )
            .setState(state, position, PLAYBACK_SPEED)
        mediaSession.setPlaybackState(stateBuilder.build())
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
            NOTIFICATION_ID,
            NotificationUtils.showNotification(this, mediaSession).build()
        )
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    companion object {
        private val TAG = MediaPlaybackService::class.java.simpleName
        private const val NOTIFICATION_ID = 1
        private const val PLAYBACK_SPEED = 1.0f
        const val TRACK_EXTRA = "track"
    }

}