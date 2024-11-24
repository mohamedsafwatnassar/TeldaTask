package com.telda.movieApp.domain.repository

import com.telda.movieApp.data.local.entity.MovieEntity

interface LocalMovieRepository {
    suspend fun fetchWatchlistMoviesFromLocal(): List<MovieEntity>

    suspend fun addMovieToWatchlist(movie: MovieEntity)

    suspend fun removeMovieFromWatchlist(movie: MovieEntity)
}
