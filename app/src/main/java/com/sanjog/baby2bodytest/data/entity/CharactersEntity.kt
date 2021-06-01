package com.sanjog.baby2bodytest.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Sanjog Shrestha on 2021/05/30.
 * Copyright (c) Sanjog Shrestha
 */
@Entity
data class CharactersEntity(@PrimaryKey
                            val id : Int,
                            val name : String,
                            val thumbnail : String?,
                            val comicID : Int)