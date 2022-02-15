package com.learprogramming.artbook.repository.di

import android.content.Context
import androidx.room.Room
import com.learprogramming.artbook.framework.repository.db.ArtDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {


    @Provides
    @Named("testDataBase")
    fun injectInMemoryRoom(@ApplicationContext context: Context) = Room
        .inMemoryDatabaseBuilder(context, ArtDataBase::class.java)
        .allowMainThreadQueries()
        .build()

}