package com.omplayer.app.activities

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.omplayer.app.R
import com.omplayer.app.callbacks.PlayerMediaSessionCallback
import com.omplayer.app.databinding.ActivityMainBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.services.MediaPlaybackService
import com.omplayer.app.utils.LibraryUtils
import com.omplayer.app.viewmodels.BaseViewModel.BaseViewEvent
import com.omplayer.app.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController: NavController by lazy { findNavController(R.id.navHostFragment) }

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val viewModel: MainViewModel by viewModels()

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

        checkExternalStoragePermission()
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

    fun playTrack(track: Track): Track {
        mediaController.transportControls.playFromUri(track.path.toUri(), Bundle().apply { putParcelable(PlayerMediaSessionCallback.TRACK_EXTRA, track) })
        return track
    }

    fun playNextTrack(track: Track): Track {
        return playTrack(getNextTrack(track))
    }

    fun playPreviousTrack(track: Track): Track {
        return playTrack(getPreviousTrack(track))
    }

    private fun getNextTrack(track: Track): Track {
        LibraryUtils.tracklist.value!!.let {
            return if (it.last() == track) {
                it.first()
            } else {
                it[it.indexOf(track) + 1]
            }
        }
    }

    private fun getPreviousTrack(track: Track): Track {
        LibraryUtils.tracklist.value!!.let {
            return if (it.first() == track) {
                it.last()
            } else {
                it[it.indexOf(track) - 1]
            }
        }
    }

    fun setTracks(tracks: List<Track>) {
    }

    private fun checkExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_STORAGE_PERMISSIONS_REQUEST
                )

            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_STORAGE_PERMISSIONS_REQUEST
                )
            }
        } else {
            viewModel.loadTracks(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadTracks(this)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 123
    }
}