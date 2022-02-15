package com.learprogramming.artbook.framework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learprogramming.artbook.framework.common.uils.Resource
import com.learprogramming.artbook.framework.common.uils.Status
import com.learprogramming.artbook.framework.repository.ArtRepoInterface
import com.learprogramming.artbook.framework.repository.data.ArtEntity
import com.learprogramming.artbook.framework.repository.data.ImageResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ArtViewModel @Inject constructor(
    private val repository: ArtRepoInterface
) : ViewModel() {

    // Art Fragment
    val artList: LiveData<List<ArtEntity>> = repository.getArt()

    // image API Fragment
    private var _images = MutableLiveData<Resource<ImageResultResponse>>()
    val images: LiveData<Resource<ImageResultResponse>>
        get() = _images

    private val _selectedImageUrl = MutableLiveData<String>()
    val selectedImageUrl: LiveData<String>
        get() = _selectedImageUrl

    // Art Detail Fragment
    private var _insertArtMsg = MutableLiveData<Resource<ArtEntity>>()
    val insertArtMsg: LiveData<Resource<ArtEntity>>
        get() = _insertArtMsg

    fun resetInsertArtMsg() {
        // neutral state
        _insertArtMsg = MutableLiveData<Resource<ArtEntity>>()
    }

    fun setSelectedImage(url : String) {
        _selectedImageUrl.postValue(url)
    }

    fun deleteArt(artEntity: ArtEntity) {
        viewModelScope.launch {
            repository.deleteArt(artEntity)
        }
    }

    fun insertArt(artEntity: ArtEntity) {
        viewModelScope.launch {
            repository.insertArt(artEntity)
        }
    }

    fun makeArt(name: String, artistName: String, year: String) {
        if (name.isEmpty() || artistName.isEmpty() || year.isEmpty()) {
            _insertArtMsg.postValue(Resource.error("Enter name, artist name, year", null))
            return
        }

        val yearInt = try {
            year.toInt()
        } catch (e: Exception) {
            _insertArtMsg.postValue(Resource.error("Year should be number", null))
            return
        }

        val artEntity = ArtEntity(name, artistName, yearInt, selectedImageUrl.value ?: "")
        insertArt(artEntity)
        setSelectedImage("")
        _insertArtMsg.postValue(Resource.success(artEntity))
    }

    fun resetImages() {
        _images = MutableLiveData<Resource<ImageResultResponse>>()
    }

    fun searchForImage(searchString: String) {
        if (searchString.isEmpty()) {
            return
        }
        _images.value = Resource.loading(null)
        viewModelScope.launch {
            val response = repository.searchImage(searchString)
            _images.postValue(response)
        }
    }
}