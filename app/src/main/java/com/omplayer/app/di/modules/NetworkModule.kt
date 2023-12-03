package com.omplayer.app.di.modules

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.omplayer.app.network.services.LastFmService
import com.omplayer.app.network.services.VideoService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
    internal fun provideLastFmService(moshi: Moshi, application: Application): LastFmService =
        Retrofit.Builder()
            .baseUrl("https://ws.audioscrobbler.com/2.0/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(ChuckerInterceptor(application.applicationContext))
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(LastFmService::class.java)

    @Provides
    @Singleton
    internal fun provideVideoService(): VideoService =
        Retrofit.Builder()
            .baseUrl("https://www.last.fm/music/")
            .build()
            .create(VideoService::class.java)
}