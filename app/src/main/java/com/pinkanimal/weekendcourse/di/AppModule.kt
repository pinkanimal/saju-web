package com.pinkanimal.weekendcourse.di

import android.content.Context
import com.pinkanimal.weekendcourse.data.repository.LlmRepository
import com.pinkanimal.weekendcourse.data.repository.LlmRepositoryImpl
import com.pinkanimal.weekendcourse.data.repository.MapRepository
import com.pinkanimal.weekendcourse.data.repository.MapRepositoryImpl
import com.pinkanimal.weekendcourse.data.repository.PlaceRepository
import com.pinkanimal.weekendcourse.data.repository.PlaceRepositoryImpl
import com.pinkanimal.weekendcourse.ocr.OcrEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOcrEngine(@ApplicationContext context: Context): OcrEngine {
        return OcrEngine(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLlmRepository(impl: LlmRepositoryImpl): LlmRepository

    @Binds
    @Singleton
    abstract fun bindPlaceRepository(impl: PlaceRepositoryImpl): PlaceRepository

    @Binds
    @Singleton
    abstract fun bindMapRepository(impl: MapRepositoryImpl): MapRepository
}
