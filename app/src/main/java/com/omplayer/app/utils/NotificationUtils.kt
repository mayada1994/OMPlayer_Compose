package com.omplayer.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.omplayer.app.R
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.services.MediaPlaybackService


object NotificationUtils {

    private const val channelId = "omplayer_channel"
    private const val channelName = "OMPlayer Channel"
    const val OPEN_PLAYER = "OPEN_PLAYER"

    private fun createNotificationChannel(context: Context) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).let { notificationManager ->
            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
                )
            }
        }
    }

    fun showNotification(
        service: MediaPlaybackService,
        mediaSession: MediaSessionCompat
    ): NotificationCompat.Builder {
        createNotificationChannel(service)

        val controller = mediaSession.controller
        val mediaMetadata: MediaMetadataCompat? = controller.metadata

        val builder = NotificationCompat.Builder(service, channelId).apply {
            // Add the metadata for the currently playing track
            setContentTitle(mediaMetadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
            setContentText(mediaMetadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
            mediaMetadata?.getBitmap(MediaMetadataCompat.METADATA_KEY_ART)?.let {
                setLargeIcon(it)
            }

            // Enable launching the player by clicking the notification
            setContentIntent(
                PendingIntent.getActivity(
                    service,
                    0,
                    Intent(service, MainActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    ).putExtra(OPEN_PLAYER, true),
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            // Make the transport controls visible on the lockscreen
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setSmallIcon(R.drawable.ic_note)

            // Take advantage of MediaStyle features
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
//                    .setShowActionsInCompactView(3)
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_prev,
                    service.getString(R.string.previous),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )

            // Add a play/pause button
            val actionPlay = NotificationCompat.Action(
                R.drawable.play,
                service.getString(R.string.play),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    service,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )

            val actionPause = NotificationCompat.Action(
                R.drawable.pause,
                service.getString(R.string.pause),
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    service,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )

            addAction(
                if (controller.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    actionPause
                } else {
                    actionPlay
                }
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.ic_next,
                    service.getString(R.string.next),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )

            addAction(
                NotificationCompat.Action(
                    R.drawable.close,
                    service.getString(R.string.close),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
            )
        }
        return builder
    }
}