package com.telda.movieApp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telda.movieApp.data.local.entity.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies")
    fun fetchAllMovies(): List<MovieEntity>

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovieById(id: Int)
}
