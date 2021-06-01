package com.sanjog.baby2bodytest.view.list

import androidx.lifecycle.*
import com.sanjog.baby2bodytest.data.Resource
import com.sanjog.baby2bodytest.data.enums.Status
import com.sanjog.baby2bodytest.data.repository.ComicRepository
import com.sanjog.baby2bodytest.utils.AbsentLiveData
import javax.inject.Inject


/**
 * Created by Sanjog Shrestha on 2021/05/31.
 * Copyright (c) Sanjog Shrestha
 */
class ComicListViewModel @Inject constructor( comicRepository: ComicRepository): ViewModel(){

    private val _yearQuery = MutableLiveData<String?>()
    val yearQuery: LiveData<String?>
        get() = _yearQuery

    private val _retry = MutableLiveData(false)

    private val nextPageHandler = NextPageHandler(comicRepository)

    fun setYear(year : String){
        _yearQuery.value = year
    }

    val getComics = _yearQuery.switchMap {
        if(it == null) AbsentLiveData.create()
        else comicRepository.fetchComics(year = it, retry = _retry.value!!)
    }

    fun loadNextPage() {
        _yearQuery.value?.let {
            if (it.isNotBlank()) {
                nextPageHandler.queryNextPage(year = it)
            }
        }
    }

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false
        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }
    }

    class NextPageHandler(private val repository: ComicRepository) :
            Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()
        private var _hasMore: Boolean = false
        val hasMore
            get() = _hasMore

        init {
            reset()
        }

        fun queryNextPage(year : String) {
            unregister()
            nextPageLiveData = repository.fetchNextPage(year = year)
            loadMoreState.value = LoadMoreState(
                    isRunning = true,
                    errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        _hasMore = result.data == true
                        unregister()
                        loadMoreState.setValue(
                                LoadMoreState(
                                        isRunning = false,
                                        errorMessage = null
                                )
                        )
                    }
                    Status.ERROR -> {
                        _hasMore = true
                        unregister()
                        loadMoreState.setValue(
                                LoadMoreState(
                                        isRunning = false,
                                        errorMessage = result.message
                                )
                        )
                    }
                    Status.LOADING -> Unit
                    Status.NOT_FOUND -> Unit
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
        }

        fun reset() {
            unregister()
            _hasMore = true
            loadMoreState.value = LoadMoreState(
                    isRunning = false,
                    errorMessage = null
            )
        }
    }

    fun refresh() {
        _retry.value = true
        _yearQuery.value?.let {
            _yearQuery.value = it
        }
    }
}