package com.example.filmcollection

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "movies.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE movies (id INTEGER PRIMARY KEY, title TEXT, genre TEXT, rating INTEGER)".trimIndent())

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS movies".trimIndent())
        onCreate(db)
    }

    fun addMovie(movie: Movie) {
        val values = ContentValues().apply {
            put("title", movie.title)
            put("genre", movie.genre)
            put("rating", movie.rating)
        }
        writableDatabase.insert("movies", null, values)
    }

    fun getAllMovies(): List<Movie> {
        val movies = mutableListOf<Movie>()
        val cursor = readableDatabase.query("movies", null, null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val title = getString(getColumnIndexOrThrow("title"))
                val genre = getString(getColumnIndexOrThrow("genre"))
                val rating = getInt(getColumnIndexOrThrow("rating"))
                movies.add(Movie(id, title, genre, rating))
            }
            close()
        }
        return movies
    }

    fun updateMovie(movie: Movie) {
        val values = ContentValues().apply {
            put("title", movie.title)
            put("genre", movie.genre)
            put("rating", movie.rating)
        }
        writableDatabase.update("movies", values, "id = ?", arrayOf(movie.id.toString()))
    }

    fun deleteMovie(id: Int) {
        writableDatabase.delete("movies", "id = ?", arrayOf(id.toString()))
    }

}