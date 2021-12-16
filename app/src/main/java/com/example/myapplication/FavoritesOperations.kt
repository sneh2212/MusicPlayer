package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.ArrayList


class FavoritesOperations(context: Context?) {
    var dbHandler: SQLiteOpenHelper
    var database: SQLiteDatabase? = null
    fun open() {
        Log.i(TAG, " Database Opened")
        database = dbHandler.writableDatabase
    }

    fun close() {
        Log.i(TAG, "Database Closed")
        dbHandler.close()
    }

    fun addSongFav(songsList: SongsList) {
        open()
        val values = ContentValues()
        values.put(FavoritesDBHandler.COLUMN_TITLE, songsList.getTitle())
        values.put(FavoritesDBHandler.COLUMN_SUBTITLE, songsList.getSubTitle())
        values.put(FavoritesDBHandler.COLUMN_PATH, songsList.getPath())
        database!!.insertWithOnConflict(
            FavoritesDBHandler.TABLE_SONGS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        close()
    }

    val allFavorites: ArrayList<Any>
        get() {
            open()
            val cursor = database!!.query(
                FavoritesDBHandler.TABLE_SONGS, allColumns,
                null, null, null, null, null
            )
            val favSongs: ArrayList<SongsList> = ArrayList<SongsList>()
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") val songsList = SongsList(
                        cursor.getString(cursor.getColumnIndex(FavoritesDBHandler.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavoritesDBHandler.COLUMN_SUBTITLE)),
                        cursor.getString(cursor.getColumnIndex(FavoritesDBHandler.COLUMN_PATH))
                    )
                    favSongs.add(songsList)
                }
            }
            close()
            return favSongs
        }

    fun removeSong(songPath: String) {
        open()
        val whereClause = FavoritesDBHandler.COLUMN_PATH + "=?"
        val whereArgs = arrayOf(songPath)
        database!!.delete(FavoritesDBHandler.TABLE_SONGS, whereClause, whereArgs)
        close()
    }

    companion object {
        const val TAG = "Favorites Database"
        private val allColumns = arrayOf(
            FavoritesDBHandler.COLUMN_ID,
            FavoritesDBHandler.COLUMN_TITLE,
            FavoritesDBHandler.COLUMN_SUBTITLE,
            FavoritesDBHandler.COLUMN_PATH
        )
    }

    init {
        dbHandler = FavoritesDBHandler(context)
    }
}
