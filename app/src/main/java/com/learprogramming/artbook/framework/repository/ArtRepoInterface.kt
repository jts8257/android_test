package com.learprogramming.artbook.framework.repository

import androidx.lifecycle.LiveData
import com.learprogramming.artbook.framework.common.uils.Resource
import com.learprogramming.artbook.framework.repository.data.ArtEntity
import com.learprogramming.artbook.framework.repository.data.ImageResultResponse


interface ArtRepoInterface {

    suspend fun insertArt(artEntity: ArtEntity)

    suspend fun deleteArt(artEntity: ArtEntity)

    fun getArt() : LiveData<List<ArtEntity>>

    suspend fun searchImage(imageString: String): Resource<ImageResultResponse>
}