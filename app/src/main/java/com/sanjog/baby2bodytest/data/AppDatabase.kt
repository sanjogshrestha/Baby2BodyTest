package com.sanjog.baby2bodytest.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sanjog.baby2bodytest.data.dao.CharacterDao
import com.sanjog.baby2bodytest.data.dao.ComicDao
import com.sanjog.baby2bodytest.data.entity.CharactersEntity
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.data.entity.CreatorsEntity
import com.sanjog.baby2bodytest.data.entity.PaginationEntity


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
@Database(entities = [
    ComicEntity::class, PaginationEntity::class, CharactersEntity::class, CreatorsEntity::class],
    version = 1,
    autoMigrations = []
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun comicDao() : ComicDao
    abstract fun characterDao() : CharacterDao
}