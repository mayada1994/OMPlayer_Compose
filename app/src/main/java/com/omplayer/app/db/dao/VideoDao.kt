package com.omplayer.app.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.omplayer.app.db.entities.Video

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos")
    suspend fun getAllVideos(): List<Video>?

    @Query("SELECT * FROM videos WHERE artist=:artist AND title=:title")
    suspend fun getVideo(artist: String, title: String): Video?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Video)

    @Delete
    suspend fun deleteVideo(video: Video)
}