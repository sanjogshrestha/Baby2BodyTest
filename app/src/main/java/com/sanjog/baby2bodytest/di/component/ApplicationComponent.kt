package com.sanjog.baby2bodytest.di.component

import com.sanjog.baby2bodytest.MainActivity
import com.sanjog.baby2bodytest.di.module.AppModule
import com.sanjog.baby2bodytest.view.detail.ComicDetailFragment
import com.sanjog.baby2bodytest.view.list.ComicListFragment
import dagger.Component
import javax.inject.Singleton


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun inject(comicListFragment: ComicListFragment)
    fun inject(comicDetailFragment: ComicDetailFragment)
}