package com.learprogramming.artbook.framework.repository.remote

import com.learprogramming.artbook.BuildConfig
import com.learprogramming.artbook.framework.repository.data.ImageResultResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {

    @GET("/api/")
    suspend fun imageSearch(
        @Query("q") searchQuery: String,
        @Query("key") key: String = BuildConfig.PIXABAY_API_KEY
    ): Response<ImageResultResponse>
}