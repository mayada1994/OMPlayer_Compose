package com.omplayer.app.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastFmImage(
    val size: String,
    @Json(name = "#text") val url: String
)