package com.sanjog.baby2bodytest.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import como.sanjog.baby2bodytest.TestUtil
import como.sanjog.baby2bodytest.getOrAwaitValue
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Sanjog Shrestha on 2021/06/01.
 * Copyright (c) Sanjog Shrestha
 */
@RunWith(AndroidJUnit4::class)
class CharacterDaoTest : DbTest(){
    /**
     * Cannot invoke observeForever on a background thread
     * Hence adding executor rule
     **/

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndReadComic() {
        val characterDao = database.characterDao()
        val charactersEntity = TestUtil.createCharacterComic(name = "foo", id = 1)
        characterDao.insert(charactersEntity)
        val loadedList = characterDao.loadCharacterByComicID(1).getOrAwaitValue()
        MatcherAssert.assertThat(loadedList, CoreMatchers.notNullValue())
        assertThat(loadedList.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(loadedList, CoreMatchers.hasItem(charactersEntity))
        val loaded = loadedList[0]
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.name, CoreMatchers.`is`("foo"))
        MatcherAssert.assertThat(loaded.comicID, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(loaded.name, CoreMatchers.notNullValue())
    }
}