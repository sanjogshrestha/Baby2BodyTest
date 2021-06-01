package com.sanjog.baby2bodytest.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import como.sanjog.baby2bodytest.TestUtil
import como.sanjog.baby2bodytest.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Sanjog Shrestha on 2021/06/01.
 * Copyright (c) Sanjog Shrestha
 */
@RunWith(AndroidJUnit4::class)
class ComicDaoTest : DbTest(){
    /**
     * Cannot invoke observeForever on a background thread
     * Hence adding executor rule
     **/

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndReadComic() {
        val comicDao = database.comicDao()
        val comicEntity = TestUtil.createComic(title = "foo", id = 1)
        comicDao.insertComic(comicEntity)
        val loaded = comicDao.loadComicDetailById(1).getOrAwaitValue()
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.title, CoreMatchers.`is`("foo"))
        MatcherAssert.assertThat(loaded.id, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(loaded.title, CoreMatchers.notNullValue())
    }

    @Test
    fun insertAndReadCharacterInComic() {
        val comicDao = database.comicDao()
        val charactersEntity = TestUtil.createCreatorForComic(name = "foo", id = 1)
        comicDao.insertCreators(charactersEntity)
        val loadedList = comicDao.loadCreatorsByComicId(1).getOrAwaitValue()
        MatcherAssert.assertThat(loadedList, CoreMatchers.notNullValue())
        assertThat(loadedList.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(loadedList, CoreMatchers.hasItem(charactersEntity))
        val loaded = loadedList[0]
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.name, CoreMatchers.`is`("foo"))
        MatcherAssert.assertThat(loaded.comicID, CoreMatchers.`is`(1))
    }
}