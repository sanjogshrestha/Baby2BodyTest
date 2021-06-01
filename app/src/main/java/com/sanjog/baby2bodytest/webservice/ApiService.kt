package com.sanjog.baby2bodytest.webservice

import androidx.lifecycle.LiveData
import com.sanjog.baby2bodytest.data.response.ApiResponse
import com.sanjog.baby2bodytest.data.response.CharacterResponse
import com.sanjog.baby2bodytest.data.response.ComicListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
interface ApiService {
    @GET("v1/public/comics")
    fun getComicsLiveData(@Query("dateRange") dateRange : String,
                  @Query("offset") offset : Int?,
                          @Query("limit") limit : Int = 20) : LiveData<ApiResponse<ComicListResponse>>

    @GET("v1/public/comics")
    fun getComics(@Query("dateRange") dateRange : String,
                  @Query("offset") offset : Int?,
                  @Query("limit") limit : Int = 20) : Call<ComicListResponse>

    @GET("/v1/public/comics/{comicId}/characters")
    fun getCharacter(@Path("comicId") comicId : Int) : LiveData<ApiResponse<CharacterResponse>>
}