package com.sanjog.baby2bodytest.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sanjog.baby2bodytest.data.entity.CharactersEntity


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: CharactersEntity)

    @Query("SELECT * FROM charactersentity WHERE comicID = :comicID")
    fun loadCharacterByComicID(comicID: Int): LiveData<List<CharactersEntity>>
}