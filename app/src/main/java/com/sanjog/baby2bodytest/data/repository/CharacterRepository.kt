package com.sanjog.baby2bodytest.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sanjog.baby2bodytest.Baby2BodyApplication
import com.sanjog.baby2bodytest.data.AppDatabase
import com.sanjog.baby2bodytest.data.Resource
import com.sanjog.baby2bodytest.data.dao.CharacterDao
import com.sanjog.baby2bodytest.data.dao.ComicDao
import com.sanjog.baby2bodytest.data.entity.CharactersEntity
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.data.entity.PaginationEntity
import com.sanjog.baby2bodytest.data.repository.utils.NetworkBoundResource
import com.sanjog.baby2bodytest.data.response.ApiSuccessResponse
import com.sanjog.baby2bodytest.data.response.CharacterResponse
import com.sanjog.baby2bodytest.data.response.ComicListResponse
import com.sanjog.baby2bodytest.utils.AbsentLiveData
import com.sanjog.baby2bodytest.utils.AppExecutors
import com.sanjog.baby2bodytest.utils.DateUtils
import com.sanjog.baby2bodytest.webservice.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


/**
 * Created by Sanjog Shrestha on 2021/05/30.
 * Copyright (c) Sanjog Shrestha
 */
@Singleton
class CharacterRepository @Inject constructor(
        private val apiService: ApiService,
        private val appExecutors: AppExecutors,
        private val database: AppDatabase,
        private val characterDao: CharacterDao,
) : CoroutineScope {

    fun fetchCharacters(comicID: Int, retry: Boolean): LiveData<Resource<List<CharactersEntity>>> {
        return object : NetworkBoundResource<List<CharactersEntity>, CharacterResponse>(appExecutors) {
            override fun saveCallResult(item: CharacterResponse) {
                val data = item.data
                val records = data.results
                database.runInTransaction {
                    for (record in records) {
                        characterDao.insert(CharactersEntity(id = record.id,
                                name = record.name,
                                comicID = comicID,
                                thumbnail = getUrl(path = record.thumbnail.path, ComicRepository.SIZE_DETAIL, record.thumbnail.extension)
                        ))
                    }
                }
            }

            override fun shouldFetch(data: List<CharactersEntity>?) : Boolean{
                return retry || data.isNullOrEmpty()
            }

            override fun loadFromDb(): LiveData<List<CharactersEntity>> {
                return characterDao.loadCharacterByComicID(comicID)
            }

            override fun createCall() = apiService.getCharacter(comicID)
        }.asLiveData()
    }

    companion object {
        const val SIZE_PORTRAIT_XLARGE = "portrait_xlarge"
        const val SIZE_STANDARD_LARGE = "standard_large"
        const val SIZE_STANDARD_INCREDIBLE = "standard_incredible"
        const val SIZE_DETAIL = "detail"
    }

    fun getUrl(path: String, size: String, extension: String): String {
        return "$path/$size.$extension"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}