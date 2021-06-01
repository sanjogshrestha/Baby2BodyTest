package com.sanjog.baby2bodytest.view.detail

import com.sanjog.baby2bodytest.data.entity.CharactersEntity


/**
 * Created by Sanjog Shrestha on 2021/03/15.
 * Copyright (c) Cloud Tech Services
 */
interface OnItemClickListener {
    /**
     * @param entity : The object is passed when clicked on view
     * This is an action when user wants to view the full screen image
     *
     **/
    fun onCharacterThumbnailClicked(entity: CharactersEntity)

    /**
     * @param url : The url is passed when clicked on view
     * This is an action when user wants to view the full screen image
     *
     **/
    fun onComicThumbnailClicked(url : String)
}