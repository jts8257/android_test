package com.learprogramming.artbook.framework.repository

import androidx.lifecycle.LiveData
import com.learprogramming.artbook.framework.common.uils.Resource
import com.learprogramming.artbook.framework.repository.data.ArtEntity
import com.learprogramming.artbook.framework.repository.data.ImageResultResponse
import com.learprogramming.artbook.framework.repository.db.ArtDao
import com.learprogramming.artbook.framework.repository.remote.RetrofitAPI
import java.lang.Exception
import javax.inject.Inject

class ArtRepo @Inject constructor(
    private val artDao: ArtDao,
    private val retrofitAPI: RetrofitAPI
): ArtRepoInterface {

    override suspend fun insertArt(artEntity: ArtEntity) {
        artDao.insertArt(artEntity)
    }

    override suspend fun deleteArt(artEntity: ArtEntity) {
        artDao.deleteArt(artEntity)
    }

    override fun getArt(): LiveData<List<ArtEntity>> {
        return artDao.observeArts()
    }

    override suspend fun searchImage(imageString: String): Resource<ImageResultResponse> {
        return try {
            val response = retrofitAPI.imageSearch(imageString)
            if (response.isSuccessful) {
                response.body()?.let { it ->
                    return@let Resource.success(it)
                } ?: Resource.error("Error", null)
            } else {
                Resource.error("Error", null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error("No data!", null)
        }
    }
}