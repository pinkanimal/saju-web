package com.pinkanimal.weekendcourse.di

import android.content.Context
import androidx.room.Room
import com.pinkanimal.weekendcourse.data.local.AppDatabase
import com.pinkanimal.weekendcourse.data.local.PlaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weekendcourse.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlaceDao(db: AppDatabase): PlaceDao = db.placeDao()
}
