package com.omplayer.app.utils

import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object ConnectivityUtils {

    fun isOnline(): Boolean {
        repeat(3) {
            if (checkNetworkAvailable()) return true
        }
        return false
    }
    private fun checkNetworkAvailable(): Boolean {
        return try {
            Socket().apply {
                connect(InetSocketAddress("8.8.8.8", 53), 1500)
                close()
            }

            true
        } catch (e: IOException) {
            false
        }
    }
}