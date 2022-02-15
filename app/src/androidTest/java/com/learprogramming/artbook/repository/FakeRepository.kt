package com.learprogramming.artbook.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.learprogramming.artbook.framework.common.uils.Resource
import com.learprogramming.artbook.framework.repository.ArtRepoInterface
import com.learprogramming.artbook.framework.repository.data.ArtEntity
import com.learprogramming.artbook.framework.repository.data.ImageResultResponse

class FakeRepository: ArtRepoInterface {

    private val arts = mutableListOf<ArtEntity>()
    private val artsLiveData = MutableLiveData<List<ArtEntity>>(arts)

    override suspend fun insertArt(artEntity: ArtEntity) {
        arts.add(artEntity)
        artsLiveData.postValue(arts)
    }

    override suspend fun deleteArt(artEntity: ArtEntity) {
        arts.remove(artEntity)
        artsLiveData.postValue(arts)
    }

    override fun getArt(): LiveData<List<ArtEntity>> {
        return artsLiveData
    }

    override suspend fun searchImage(imageString: String): Resource<ImageResultResponse> {
        return Resource.success(ImageResultResponse(listOf(), 0, 0))
    }

    private fun refreshData() {
        artsLiveData.postValue(arts)
    }
}