package com.omplayer.app.services

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import com.omplayer.app.R
import com.omplayer.app.callbacks.PlayerMediaSessionCallback
import com.omplayer.app.utils.NotificationUtils


class MediaPlaybackService: MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat

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

            setCallback(
                PlayerMediaSessionCallback(
                    applicationContext,
                    this,
                    object : PlayerMediaSessionCallback.OnMediaSessionStoppedListener {
                        override fun onStop() {
                            stopForeground(Service.STOP_FOREGROUND_REMOVE)
                            NotificationManagerCompat.from(this@MediaPlaybackService).cancel(NOTIFICATION_ID)
                        }
                    })
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            NotificationUtils.showNotification(
                this@MediaPlaybackService,
                mediaSession
            ).build()
        )
        return super.onStartCommand(intent, flags, startId)
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

    companion object {
        private val TAG = MediaPlaybackService::class.java.simpleName
        private const val NOTIFICATION_ID = 1
    }

}