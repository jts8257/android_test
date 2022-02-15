package com.learprogramming.artbook.framework.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.learprogramming.artbook.BuildConfig
import com.learprogramming.artbook.R
import com.learprogramming.artbook.framework.repository.ArtRepo
import com.learprogramming.artbook.framework.repository.ArtRepoInterface
import com.learprogramming.artbook.framework.repository.db.ArtDao
import com.learprogramming.artbook.framework.repository.db.ArtDataBase
import com.learprogramming.artbook.framework.repository.remote.RetrofitAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectRoomDataBase(@ApplicationContext context: Context): ArtDataBase = Room.databaseBuilder(
        context, ArtDataBase::class.java, "art.db").build()

    @Singleton
    @Provides
    fun injectDao(dataBase: ArtDataBase): ArtDao = dataBase.artDao()

    @Singleton
    @Provides
    fun injectRetrofitApi(): RetrofitAPI {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.PIXABAY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitAPI::class.java)
    }

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context): RequestManager = Glide
        .with(context).setDefaultRequestOptions(
            RequestOptions().placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
        )

    @Singleton
    @Provides
    fun injectRepository(artDao: ArtDao, retrofitAPI: RetrofitAPI): ArtRepoInterface =
        ArtRepo(artDao, retrofitAPI)
}