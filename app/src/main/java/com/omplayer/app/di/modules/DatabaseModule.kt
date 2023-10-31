package com.omplayer.app.di.modules

import android.app.Application
import androidx.room.Room
import com.omplayer.app.db.PlayerDatabase
import com.omplayer.app.db.dao.PlaylistDao
import com.omplayer.app.db.dao.ScrobbledTrackDao
import com.omplayer.app.db.dao.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideDatabase(application: Application): PlayerDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            PlayerDatabase::class.java, "omplayer.db"
        ).build()
    }

    @Provides
    @Singleton
    internal fun providePlaylistDao(database: PlayerDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    @Singleton
    internal fun provideScrobbledTrackDao(database: PlayerDatabase): ScrobbledTrackDao {
        return database.scrobbledTrackDao()
    }

    @Provides
    @Singleton
    internal fun provideVideoDao(database: PlayerDatabase): VideoDao {
        return database.videoDao()
    }
}