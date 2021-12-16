package com.example.musicplayer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class FavoritesDBHandler(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS)
        db.execSQL(TABLE_CREATE)
    }

    companion object {
        private const val DATABASE_NAME = "favorites.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_SONGS = "favorites"
        const val COLUMN_ID = "songID"
        const val COLUMN_TITLE = "title"
        const val COLUMN_SUBTITLE = "subtitle"
        const val COLUMN_PATH = "songpath"
        private const val TABLE_CREATE = ("CREATE TABLE " + TABLE_SONGS + " (" + COLUMN_ID
                + " INTEGER, " + COLUMN_TITLE + " TEXT, " + COLUMN_SUBTITLE
                + " TEXT, " + COLUMN_PATH + " TEXT PRIMARY KEY " + ")")
    }
}
