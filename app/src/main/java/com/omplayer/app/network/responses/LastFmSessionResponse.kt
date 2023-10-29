package com.omplayer.app.network.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastFmSessionResponse(val session: LastFmSession) {

    @JsonClass(generateAdapter = true)
    data class LastFmSession(
        val subscriber: String,
        val name: String,
        val key: String
    )
}
