package com.sanjog.baby2bodytest.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sanjog.baby2bodytest.data.repository.CharacterRepository
import com.sanjog.baby2bodytest.data.repository.ComicRepository
import com.sanjog.baby2bodytest.utils.AbsentLiveData
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Sanjog Shrestha on 2021/05/31.
 * Copyright (c) Sanjog Shrestha
 */
class ComicDetailViewModel @Inject constructor(comicRepository: ComicRepository,
                                               private val characterRepository: CharacterRepository): ViewModel() {
    private val _comicIDQuery = MutableLiveData<Int>()

    fun setComicID(comicID : Int){
        this._comicIDQuery.value = comicID
    }

    val getComicDetail = _comicIDQuery.switchMap {
        if(it == null) AbsentLiveData.create()
        else comicRepository.loadComicDetailById(comicID = it)
    }

    val getComicCreators = _comicIDQuery.switchMap {
        if(it == null) AbsentLiveData.create()
        else comicRepository.loadCreatorsOfComic(comicID = it)
    }

    val fetchCharacters = _comicIDQuery.switchMap {
        if(it == null) AbsentLiveData.create()
        else characterRepository.fetchCharacters(comicID = it, false)
    }
}