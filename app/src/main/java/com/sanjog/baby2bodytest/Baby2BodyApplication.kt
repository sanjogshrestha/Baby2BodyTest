package com.sanjog.baby2bodytest

import android.app.Application
import android.content.Context
import com.bugfender.sdk.Bugfender
import com.sanjog.baby2bodytest.di.component.ApplicationComponent
import com.sanjog.baby2bodytest.di.component.DaggerApplicationComponent
import com.sanjog.baby2bodytest.di.module.AppModule
import com.sanjog.baby2bodytest.utils.BugfenderTree
import com.sanjog.baby2bodytest.utils.RateLimiter
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) sanjogshrestha.nepal@gmail.com
 */
class Baby2BodyApplication : Application(){
    lateinit var mApplicationComponent: ApplicationComponent

    init {
        instance = this
        initDagger()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
            Timber.plant(BugfenderTree())
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Bugfender.init(this, "6IdkxRsWEldpNeXeTY6NktQOwoM6vImW", BuildConfig.DEBUG)
            Bugfender.enableCrashReporting()
            Bugfender.enableUIEventLogging(this)
            Bugfender.enableLogcatLogging()
        }
    }


    private fun initDagger() {
        mApplicationComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        private var instance: Baby2BodyApplication? = null

        fun getApplicationComponent(): ApplicationComponent {
            return instance!!.mApplicationComponent
        }

        fun getApplicationContext() : Context {
            return instance!!.applicationContext
        }

        val ComicListRateLimit = RateLimiter<String>(1, TimeUnit.DAYS)
        val CharacterRateLimit = RateLimiter<String>(1, TimeUnit.DAYS)
    }
}