package como.sanjog.baby2bodytest

import com.sanjog.baby2bodytest.data.entity.CharactersEntity
import com.sanjog.baby2bodytest.data.entity.ComicEntity
import com.sanjog.baby2bodytest.data.entity.CreatorsEntity

/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) sanjogshrestha.nepal@gmail.com
 */
object TestUtil {
    fun createComic(title: String, id : Int) = ComicEntity(
        id = id,
        digitalId = title.hashCode(),
        title = title,
        variantDescription = "",
        description = "",
        modified = "",
        isbn = "",
        upc = "",
        diamondCode = "",
        ean = "",
        issn = "",
        format = "comic",
        pageCount = 1,
        printPrice = 1.0,
        digitalPrintPrice = 1.0,
        onSaleDate = "",
        focDate = "",
        thumbnail = "",
        isFavourite = 1,
        yearOfRelease = "",
        images = null,
        issueNumber = 1
    )

    fun createCreatorForComic(name: String, id : Int) = CreatorsEntity(
        id = id.hashCode().toString(),
        name = name,
        role = "role",
        comicID = id
    )

    fun createCharacterComic(name: String, id : Int) = CharactersEntity(
        id = id.hashCode(),
        name = name,
        thumbnail = "",
        comicID = id
    )
}