package com.pinkanimal.weekendcourse.di

import android.content.Context
import com.pinkanimal.weekendcourse.ocr.OcrEngine
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
