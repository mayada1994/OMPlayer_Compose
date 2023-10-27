package com.omplayer.app.network.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastFmSimilarTracksResponse(
    @Json(name = "similartracks") val similarTracks: LastFmSimilarTracks
) {

    @JsonClass(generateAdapter = true)
    data class LastFmSimilarTracks(
        @Json(name = "track") val similarTracksList: List<LastFmSimilarTrack>
    )

    @JsonClass(generateAdapter = true)
    data class LastFmSimilarTrack(
        val name: String,
        val match: Float,
        val url: String,
        val artist: LastFmSimilarTrackArtist
    )

    @JsonClass(generateAdapter = true)
    data class LastFmSimilarTrackArtist(val name: String)
}
