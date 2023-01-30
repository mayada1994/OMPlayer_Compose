package com.omplayer.app.network.responses

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastFmSimilarTracksResponse(val similarTracks: LastFmSimilarTracks) {

    @JsonClass(generateAdapter = true)
    data class LastFmSimilarTracks(val similarTracksList: List<LastFmSimilarTrack>)

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
