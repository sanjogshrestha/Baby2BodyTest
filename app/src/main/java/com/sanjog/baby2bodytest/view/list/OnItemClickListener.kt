package com.sanjog.baby2bodytest.view.list

import android.view.View
import com.sanjog.baby2bodytest.data.entity.ComicEntity


/**
 * Created by Sanjog Shrestha on 2021/03/15.
 * Copyright (c) Cloud Tech Services
 */
interface OnItemClickListener {
    /**
     * @param entity : The object is passed when clicked on view
     * This is an action when user wants to navigate to detail screen
     *
     **/
    fun onComicThumbnailClicked(entity: ComicEntity, view: View)
}