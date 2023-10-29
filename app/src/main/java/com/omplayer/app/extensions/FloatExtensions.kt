package com.omplayer.app.extensions

fun Float.round(decimals: Int = 2): String = "%.${decimals}f".format(this)