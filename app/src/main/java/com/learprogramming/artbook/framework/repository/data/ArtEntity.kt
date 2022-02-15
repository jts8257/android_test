package com.learprogramming.artbook.framework.repository.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "art")
data class ArtEntity(
    var name: String,
    var artistname: String,
    var year: Int,
    @ColumnInfo(name = "image_url")
    var imageUrl: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
)
