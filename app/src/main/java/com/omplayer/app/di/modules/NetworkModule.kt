package com.omplayer.app.di.modules

import com.omplayer.app.network.services.LastFmService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideMoshi() = Moshi.Builder().build()

    @Provides
    @Singleton
    internal fun provideLastFmService(moshi: Moshi): LastFmService =
        Retrofit.Builder()
            .baseUrl("https://ws.audioscrobbler.com/2.0/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(LastFmService::class.java)
}