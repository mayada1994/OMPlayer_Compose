package com.omplayer.app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omplayer.app.db.dao.VideoDao
import com.omplayer.app.db.entities.Video

@Database(entities = [Video::class], version = 1)
abstract class PlayerDatabase: RoomDatabase() {

    abstract fun videoDao(): VideoDao
}