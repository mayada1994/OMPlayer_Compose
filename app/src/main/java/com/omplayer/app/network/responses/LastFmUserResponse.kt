package com.omplayer.app.network.responses

import com.omplayer.app.entities.LastFmImage
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastFmUserResponse(
    val user: LastFmUser
) {
    @JsonClass(generateAdapter = true)
    data class LastFmUser(
        @Json(name = "image") val images: List<LastFmImage>
    )
}