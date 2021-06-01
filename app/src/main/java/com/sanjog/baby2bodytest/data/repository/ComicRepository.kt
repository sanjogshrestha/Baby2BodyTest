package com.sanjog.baby2bodytest.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.sanjog.baby2bodytest.Baby2BodyApplication
import com.sanjog.baby2bodytest.data.AppDatabase
import com.sanjog.baby2bodytest.data.Resource
import com.sanjog.baby2bodytest.data.dao.ComicDao
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.data.entity.CreatorsEntity
import com.sanjog.baby2bodytest.data.entity.PaginationEntity
import com.sanjog.baby2bodytest.data.repository.utils.NetworkBoundResource
import com.sanjog.baby2bodytest.data.response.ApiSuccessResponse
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
class ComicRepository @Inject constructor(private val apiService: ApiService,
                                          private val appExecutors: AppExecutors,
                                          private val database: AppDatabase,
                                          private val comicDao: ComicDao
) : CoroutineScope {
    fun fetchNextPage(year: String): LiveData<Resource<Boolean>> {
        val dateRange = "$year-01-01,$year-12-31"
        val fetchNextPageTask =
                FetchNextComicListPageTask(
                        type = dateRange,
                        apiService = apiService,
                        db = database
                )
        appExecutors.networkIO().execute(fetchNextPageTask)
        return fetchNextPageTask.liveData
    }

    fun fetchComics(year: String, retry: Boolean): LiveData<Resource<List<ComicEntity>>> {
        val dateRange = "$year-01-01,$year-12-31"
        return object : NetworkBoundResource<List<ComicEntity>, ComicListResponse>(appExecutors) {
            override fun saveCallResult(item: ComicListResponse) {
                val data = item.data
                val records = data.results
                val comicIds = records.map { it.id }
                val paginationResult = PaginationEntity(
                        type = dateRange,
                        ids = comicIds,
                        totalCount = data.total,
                        next = if(data.total > data.count) data.count else null
                )
                database.runInTransaction {
                    for (record in records) {
                        var printPrice  : Double = 0.0
                        var digitalPrintPrice : Double = 0.0
                        record.prices.forEach {
                            if(it.type.equals("printPrice", true)) {
                                printPrice = it.price
                            } else digitalPrintPrice = it.price
                        }

                        var onSaleDate = String()
                        var focDate = String()
                        record.dates.forEach {
                            if(it.type.equals("onsaleDate", true)) {
                                onSaleDate = it.date
                            } else focDate = it.date
                        }

                        val imageList = ArrayList<String>()
                        record.images.forEach {
                            imageList.add(getUrl(path = it.path, SIZE_DETAIL, it.extension))
                        }

                        val creators = record.creators
                        creators.items?.forEach {
                            comicDao.insertCreators(CreatorsEntity(comicID = record.id,
                                    name = it.name.capitalize(),
                                    role = it.role.capitalize(),
                                    id = "${it.name}_${it.role}"))
                        }

                        comicDao.insertComic(ComicEntity(id = record.id,
                                digitalId = record.digitalId,
                                title = record.title,
                                issueNumber = record.issueNumber,
                                variantDescription = record.variantDescription,
                                description = record.description,
                                modified = record.modified,
                                isbn = record.isbn,
                                upc = record.upc,
                                diamondCode = record.diamondCode,
                                ean = record.ean,
                                issn = record.issn,
                                format = record.format,
                                pageCount = record.pageCount,
                                printPrice = printPrice,
                                digitalPrintPrice = digitalPrintPrice,
                                onSaleDate = onSaleDate,
                                focDate = focDate,
                                yearOfRelease = DateUtils.getYear(focDate),
                                thumbnail = getUrl(path = record.thumbnail.path, SIZE_DETAIL, record.thumbnail.extension),
                                images = imageList
                        ))
                    }
                    comicDao.insert(paginationResult)
                }
            }

            override fun shouldFetch(data: List<ComicEntity>?) : Boolean{
                return retry || data.isNullOrEmpty()
            }

            override fun loadFromDb(): LiveData<List<ComicEntity>> {
                return comicDao.search(dateRange).switchMap {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else {
                        comicDao.loadComicOrdered(it.ids)
                    }
                }
            }

            override fun processResponse(response: ApiSuccessResponse<ComicListResponse>)
                    : ComicListResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }

            override fun createCall() = apiService.getComicsLiveData(dateRange, 0)
        }.asLiveData()
    }

    fun loadCreatorsOfComic(comicID : Int) = comicDao.loadCreatorsByComicId(comicID)
    fun loadComicDetailById(comicID : Int) = comicDao.loadComicDetailById(comicID)

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