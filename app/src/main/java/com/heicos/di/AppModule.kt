package com.heicos.di

import android.app.Application
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.heicos.data.database.CosplaysDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(
        app: Application
    ): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideCosplayDataBase(
        applicationContext: Context
    ): CosplaysDataBase {
        return Room.databaseBuilder(
            applicationContext,
            CosplaysDataBase::class.java,
            "cosplay.db"
        ).build()
    }

    @Provides
    fun provideVideoPlayer(app: Application): ExoPlayer {
        return ExoPlayer.Builder(app)
            .build()
    }
}