package com.sanjog.baby2bodytest.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Sanjog Shrestha on 2021/05/30.
 * Copyright (c) Sanjog Shrestha
 */
@Entity
data class CreatorsEntity(@PrimaryKey
                          val id : String,
                          val name : String,
                          val role : String,
                          val comicID : Int)