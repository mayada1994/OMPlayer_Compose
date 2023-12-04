package com.omplayer.app.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.omplayer.app.R

@SuppressLint("ConflictingOnColor")
@Composable
fun OMPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColors = lightColors(
        primary = colorResource(id = R.color.colorBackground),
        primaryVariant = colorResource(id = R.color.colorBackgroundDark),
        secondary = colorResource(id = R.color.colorBackgroundDark),
        background = colorResource(id = R.color.colorBackground),
        onPrimary = colorResource(id = R.color.colorTextPrimary),
        onSecondary = colorResource(id = R.color.colorTextSecondary),
    )

    val darkColors = darkColors(
        primary = colorResource(id = R.color.colorBackground),
        primaryVariant = colorResource(id = R.color.colorBackgroundDark),
        secondary = colorResource(id = R.color.colorBackgroundDark),
        background = colorResource(id = R.color.colorBackground),
        onPrimary = colorResource(id = R.color.colorTextPrimary),
        onSecondary = colorResource(id = R.color.colorTextSecondary),
    )

    MaterialTheme(
        colors = if (darkTheme) darkColors else lightColors,
        content = content
    )
}