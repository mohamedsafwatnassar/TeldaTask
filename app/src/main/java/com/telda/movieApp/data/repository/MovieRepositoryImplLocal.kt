package com.telda.movieApp.data.repository

import com.telda.movieApp.data.common.BaseRepo
import com.telda.movieApp.data.local.dao.MovieDao
import com.telda.movieApp.data.local.entity.MovieEntity
import com.telda.movieApp.domain.repository.LocalMovieRepository
import javax.inject.Inject

class MovieRepositoryImplLocal @Inject constructor(
    private val movieDao: MovieDao
) : BaseRepo(), LocalMovieRepository {

    override suspend fun fetchWatchlistMoviesFromLocal(): List<MovieEntity> {
        return movieDao.fetchAllMovies()
    }

    override suspend fun addMovieToWatchlist(movie: MovieEntity) {
        movieDao.insertMovie(MovieEntity(movie.id))
    }

    override suspend fun removeMovieFromWatchlist(movie: MovieEntity) {
        movieDao.deleteMovieById(movie.id)
    }
}




