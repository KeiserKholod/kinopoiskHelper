package com.example.kinopoiskparser.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.kinopoiskparser.data.FilmsStorageContract.FilmStorage
import android.database.sqlite.SQLiteOpenHelper


class FilmsDbHelper
/**
 * Конструктор [FilmsDbHelper].
 *
 * @param context Контекст приложения
 */
    (context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    /**
     * Вызывается при создании базы данных
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Строка для создания таблицы
        val SQL_CREATE_GUESTS_TABLE = ("CREATE TABLE " + FilmStorage.TABLE_NAME.toString() + " ("
                + FilmStorage._ID.toString() + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FilmStorage.COLUMN_NAME.toString() + " TEXT NOT NULL, "
                + FilmStorage.COLUMN_RATE.toString() + " TEXT NOT NULL, "
                + FilmStorage.COLUMN_YEAR.toString() + " TEXT NOT NULL, "
                + FilmStorage.COLUMN_DESC.toString() + " TEXT NOT NULL, "
                + FilmStorage.COLUMN_IMAGE_URL.toString() + " TEXT NOT NULL")

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_GUESTS_TABLE)
    }

    /**
     * Вызывается при обновлении схемы базы данных
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        val LOG_TAG = FilmsDbHelper::class.java.simpleName

        /**
         * Имя файла базы данных
         */
        private const val DATABASE_NAME = "FilmsStorage.db"

        /**
         * Версия базы данных. При изменении схемы увеличить на единицу
         */
        private const val DATABASE_VERSION = 1
    }
}