package com.sanjog.baby2bodytest.data.repository

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sanjog.baby2bodytest.data.AppDatabase
import com.sanjog.baby2bodytest.data.Resource
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.data.entity.CreatorsEntity
import com.sanjog.baby2bodytest.data.entity.PaginationEntity
import com.sanjog.baby2bodytest.data.response.*
import com.sanjog.baby2bodytest.utils.DateUtils
import com.sanjog.baby2bodytest.webservice.ApiService
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
class FetchNextComicListPageTask constructor(
        private val apiService: ApiService,
        private val db: AppDatabase,
        private val type: String
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
        val comicDao = db.comicDao()
        val current = comicDao.findPaginationResult(type)
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = current.next

        if (nextPage == null) {
            _liveData.postValue(Resource.success(false))
            return
        }

        val hasMore = current.totalCount > nextPage + current.ids.size

        if(!hasMore) {
            _liveData.postValue(Resource.success(false))
            return
        }

        val nextPageNumber = current.ids.size
        val newValue = try {
            val response = apiService.getComics(offset = nextPageNumber, dateRange = type).execute()

            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    // we merge all repo ids into 1 list so that it is easier to fetch the
                    // result list.
                    val data = apiResponse.body.data
                    val results = data.results
                    val ids = arrayListOf<Int>()
                    ids.addAll(current.ids)
                    ids.addAll(results.map {it.id })

                    val merged = PaginationEntity(
                            type = type,
                            ids = ids,
                            totalCount = data.total,
                            next = nextPageNumber
                    )

                    db.runInTransaction {
                        for (record in results) {
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
                                imageList.add(getUrl(path = it.path, ComicRepository.SIZE_DETAIL, it.extension))
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
                        comicDao.insert(merged)
                    }
                    Resource.success(apiResponse.body.nextPage != null)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
                is ApiNotFoundResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: Throwable){
            val errorMessage = when (e) {
                is HttpException -> "An error has occurred: ${e.code()}\nPlease try again."
                is SocketTimeoutException -> "A timeout error has occurred,\nplease check your internet connection and try again"
                is ConnectException, is IOException -> "An error has occurred, most likely a network issue.\nPlease check your internet connection and try again"
                else -> e.message
            }
            Resource.error(errorMessage!!, true)
        }
        _liveData.postValue(newValue)
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

}
