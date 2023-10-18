package com.heicos.di

import com.heicos.data.repository.CosplayRepositoryImpl
import com.heicos.domain.repository.CosplayRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCosplayRepository(
        cosplayRepository: CosplayRepositoryImpl
    ): CosplayRepository

}