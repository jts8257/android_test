package com.learprogramming.artbook.framework.repository.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.learprogramming.artbook.framework.repository.data.ArtEntity

@Dao
interface ArtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArt(artEntity: ArtEntity)

    @Delete
    suspend fun deleteArt(artEntity: ArtEntity)

    @Query("SELECT * FROM art WHERE name =:name")
    suspend fun getArtByName(name: String): ArtEntity?

    @Query("SELECT * FROM art")
    suspend fun getAllArt(): List<ArtEntity>

    @Query("SELECT * FROM art")
    fun observeArts(): LiveData<List<ArtEntity>>
}