package com.heicos.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.heicos.data.database.SearchQueryDao
import com.heicos.data.database.SearchQueryDataBase
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
    fun provideSearchQueryDao(
        applicationContext: Context
    ): SearchQueryDao {
        return Room.databaseBuilder(
            applicationContext,
            SearchQueryDataBase::class.java,
            "queries.db"
        ).build().dao
    }
}