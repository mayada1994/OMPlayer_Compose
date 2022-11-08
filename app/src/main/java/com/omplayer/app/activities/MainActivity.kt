package com.omplayer.app.activities

import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.omplayer.app.R
import com.omplayer.app.databinding.ActivityMainBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.services.MediaPlaybackService
import com.omplayer.app.viewmodels.BaseViewModel.BaseViewEvent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController: NavController by lazy { findNavController(R.id.navHostFragment) }

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {

            // Get the token for the MediaSession
            mediaBrowser.sessionToken.also { token ->

                // Create a MediaControllerCompat and save it
                MediaControllerCompat.setMediaController(
                    this@MainActivity,
                    MediaControllerCompat(this@MainActivity, token)
                )
            }

            Log.d(TAG, "onConnected")
        }

        override fun onConnectionSuspended() {
            Log.d(TAG, "onConnectionSuspended")
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        override fun onConnectionFailed() {
            Log.d(TAG, "onConnectionFailed")
            // The Service has refused our connection
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).also { binding = it }.root)

        mediaBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, MediaPlaybackService::class.java),
            connectionCallbacks,
            null // optional Bundle
        )
    }

    fun handleBaseEvent(event: ViewEvent) {
        when (event) {
            is BaseViewEvent.Navigate -> navController.navigate(event.navDirections)
        }
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onStop() {
        super.onStop()
        mediaBrowser.disconnect()
    }

    fun playTrack(track: Track) {
        ContextCompat.startForegroundService(this, Intent(this, MediaPlaybackService::class.java))
        mediaController.transportControls.playFromUri(track.path.toUri(), Bundle().apply { putParcelable(MediaPlaybackService.TRACK_EXTRA, track) })
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}