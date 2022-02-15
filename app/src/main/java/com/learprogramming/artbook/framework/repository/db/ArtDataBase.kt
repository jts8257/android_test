package com.learprogramming.artbook.framework.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learprogramming.artbook.framework.repository.data.ArtEntity

@Database(entities = [ArtEntity::class], version = 1)
abstract class ArtDataBase: RoomDatabase() {
    abstract fun artDao(): ArtDao
}
