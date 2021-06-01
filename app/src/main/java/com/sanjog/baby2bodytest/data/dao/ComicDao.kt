package com.sanjog.baby2bodytest.data.dao

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.data.entity.CreatorsEntity
import com.sanjog.baby2bodytest.data.entity.PaginationEntity


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
@Dao
interface ComicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(result: PaginationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComic(entity: ComicEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCreators(entity: CreatorsEntity)

    @Query("SELECT * FROM paginationentity WHERE type = :type")
    fun search(type: String): LiveData<PaginationEntity?>

    fun loadComicOrdered(ids: List<Int>): LiveData<List<ComicEntity>> {
        val order = SparseIntArray()
        ids.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return loadComicById(ids).map { repositories ->
            repositories.sortedWith(compareBy { order.get(it.id) })
        }
    }

    @Query("SELECT * FROM comicentity WHERE id in (:ids)")
    fun loadComicById(ids: List<Int>): LiveData<List<ComicEntity>>

    @Query("SELECT * FROM comicentity WHERE id = :comicID")
    fun loadComicDetailById(comicID: Int): LiveData<ComicEntity>

    @Query("SELECT * FROM creatorsentity WHERE comicID = :comicID")
    fun loadCreatorsByComicId(comicID: Int): LiveData<List<CreatorsEntity>>

    @Query("SELECT * FROM paginationentity WHERE type = :type")
    fun findPaginationResult(type: String): PaginationEntity?

    @Query("SELECT * FROM paginationentity WHERE type = :type")
    fun searchPaginationResult(type: String): LiveData<PaginationEntity?>
}