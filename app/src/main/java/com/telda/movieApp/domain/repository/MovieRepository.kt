package com.telda.movieApp.domain.repository

import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.domain.model.MovieCredits
import com.telda.movieApp.domain.model.MovieDetails
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun fetchPopularMovies(page: Int): Flow<DataState<List<Movie>>>

    suspend fun searchMovies(query: String, page: Int): Flow<DataState<List<Movie>>>

    suspend fun fetchMovieDetailsById(movieId: Int): Flow<DataState<MovieDetails>>

    suspend fun fetchSimilarMovies(movieId: Int): Flow<DataState<List<Movie>>>

    suspend fun fetchMovieCreditsById(movieId: Int): Flow<DataState<MovieCredits>>

}
