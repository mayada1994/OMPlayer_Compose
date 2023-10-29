package com.omplayer.app.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.omplayer.app.utils.CacheManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScrobblingTileService: TileService() {

    @Inject
    lateinit var cacheManager: CacheManager

    companion object {
        private val TAG = ScrobblingTileService::class.java.simpleName
    }

    // Called when the user adds your tile.
    override fun onTileAdded() {
        Log.d(TAG, "onTileAdded()")
    }

    // Called when your app can update your tile.
    override fun onStartListening() {
        Log.d(TAG, "onStartListening()")
        updateTileState()
    }

    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        Log.d(TAG, "onStopListening()")
    }

    // Called when the user taps on your tile in an active or inactive state.
    override fun onClick() {
        Log.d(TAG, "onClick()")
        cacheManager.isScrobblingEnabled = !cacheManager.isScrobblingEnabled
        updateTileState()
    }

    // Called when the user removes your tile.
    override fun onTileRemoved() {
        Log.d(TAG, "onTileRemoved()")
    }

    private fun updateTileState() {
        qsTile.state = when {
            cacheManager.currentLastFmSession == null -> Tile.STATE_UNAVAILABLE
            cacheManager.isScrobblingEnabled -> Tile.STATE_ACTIVE
            else -> Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }
}