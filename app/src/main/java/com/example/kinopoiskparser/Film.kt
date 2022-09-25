package com.example.kinopoiskparser

import android.graphics.Bitmap
import java.util.jar.Attributes.Name

class Film(
    FilmName: String,
    FilmYear: String,
    FilmRate: String,
    FilmDesc: String,
    FilmImage: Bitmap?,
    FilmImageURL: String
) {
    val filmName: String
    val filmRate: String
    val filmYear: String
    val filmDesc: String
    val filmImage: Bitmap?
    val filmImageURL: String
    val allDataJSON: String = ""
    var filmDbId: String = ""

    init {
        filmName = FilmName
        filmRate = FilmRate
        filmYear = FilmYear
        filmImage = FilmImage
        filmImageURL = FilmImageURL
        filmDesc = FilmDesc
    }
}