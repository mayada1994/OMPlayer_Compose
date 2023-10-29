package com.omplayer.app.activities

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.omplayer.app.R
import com.omplayer.app.callbacks.PlayerMediaSessionCallback
import com.omplayer.app.databinding.ActivityMainBinding
import com.omplayer.app.entities.Track
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.services.MediaPlaybackService
import com.omplayer.app.viewmodels.BaseViewModel.BaseViewEvent
import com.omplayer.app.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        viewModel.checkForOfflineScrobbledTracks(this)
    }

    /**
     * Returns true if the event is base and false if it is custom
     */
    fun handleBaseEvent(event: ViewEvent): Boolean {
        return when (event) {
            is BaseViewEvent.NavigateUp -> {
                navController.navigateUp()
                true
            }
            is BaseViewEvent.Navigate -> {
                navController.navigate(event.navDirections)
                true
            }
            is BaseViewEvent.ShowError -> {
                Toast.makeText(this, event.resId, event.duration).show()
                true
            }
            is BaseViewEvent.PausePlayback -> {
                mediaController.transportControls.pause()
                true
            }
            else -> false
        }
    }

    fun showProgress(isVisible: Boolean) {
        binding.progressBar.isVisible = isVisible
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
        mediaController.transportControls.playFromUri(
            track.path.toUri(),
            Bundle().apply { putParcelable(PlayerMediaSessionCallback.TRACK_EXTRA, track) }
        )
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