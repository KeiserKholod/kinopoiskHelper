package com.example.kinopoiskparser.data

import android.provider.BaseColumns




class FilmsStorageContract {
    private fun filmsContract() {}

    object FilmStorage : BaseColumns {
        const val TABLE_NAME = "Films"
        const val _ID = BaseColumns._ID
        const val COLUMN_NAME = "name"
        const val COLUMN_RATE = "rate"
        const val COLUMN_YEAR = "year"
        const val COLUMN_DESC = "description"
        const val COLUMN_IMAGE_URL = "ImageURL"

    }
}