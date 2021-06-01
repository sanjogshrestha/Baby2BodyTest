package com.sanjog.baby2bodytest.di.module

import android.content.Context
import androidx.room.Room
import com.sanjog.baby2bodytest.BuildConfig
import com.sanjog.baby2bodytest.data.AppDatabase
import com.sanjog.baby2bodytest.data.dao.CharacterDao
import com.sanjog.baby2bodytest.data.dao.ComicDao
import com.sanjog.baby2bodytest.utils.HashHelper
import com.sanjog.baby2bodytest.utils.LiveDataCallAdapterFactory
import com.sanjog.baby2bodytest.webservice.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
@Module(includes = [ViewModelModule::class])
class AppModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideDatabase(): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, BuildConfig.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(retrofitBuilder: Retrofit.Builder): ApiService {
        return retrofitBuilder
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }


    @Singleton
    @Provides
    fun getRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_MARVEL_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
    }


    @Singleton
    @Provides
    fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.interceptors().clear()
        builder
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                )
            )
            .addInterceptor { chain: Interceptor.Chain ->
                val currentRequest = chain.request()
                val currentTimeInMillis = System.currentTimeMillis().toString()
                val hash = HashHelper.generate(currentTimeInMillis
                        + BuildConfig.MARVEL_PRIVATE_KEY
                        + BuildConfig.MARVEL_PUBLIC_KEY)

                val url: HttpUrl = currentRequest.url.newBuilder()
                    .addQueryParameter(QUERY_NAME_TIMESTAMP, currentTimeInMillis)
                    .addQueryParameter(QUERY_NAME_API_KEY, BuildConfig.MARVEL_PUBLIC_KEY)
                    .addQueryParameter(QUERY_NAME_HASH, hash).build()

                val requestBuilder = currentRequest
                    .newBuilder()
                    .header("Accept", "application/json")
                    .url(url)
                    .build()
                chain.proceed(requestBuilder)
            }
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
        return builder.build()
    }

    @Singleton @Provides
    fun provideComicDao(db: AppDatabase): ComicDao {
        return db.comicDao()
    }

    @Singleton @Provides
    fun provideCharacterDao(db: AppDatabase): CharacterDao {
        return db.characterDao()
    }

    companion object {
        private const val QUERY_NAME_TIMESTAMP = "ts"
        private const val QUERY_NAME_API_KEY = "apikey"
        private const val QUERY_NAME_HASH = "hash"
    }
}