package com.sanjog.baby2bodytest.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Sanjog Shrestha on 2021/06/01.
 * Copyright (c) Sanjog Shrestha
 */

class SpaceItemDecoration(context: Context, @DimenRes spaceRes: Int) : RecyclerView.ItemDecoration() {

    private val space = context.resources.getDimensionPixelSize(spaceRes)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(space, space, space, space)
    }
}