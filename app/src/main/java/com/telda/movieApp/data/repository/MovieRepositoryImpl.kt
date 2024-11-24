package com.telda.movieApp.data.repository

import com.telda.movieApp.data.api.ApiService
import com.telda.movieApp.data.common.BaseRepo
import com.telda.movieApp.domain.model.CustomError
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.domain.model.MovieCredits
import com.telda.movieApp.domain.model.MovieDetails
import com.telda.movieApp.domain.repository.MovieRepository
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.telda.movieApp.data.mapper.MovieCreditsMapper.mapToDomain as mapCreditsToDomain
import com.telda.movieApp.data.mapper.MovieDetailsMapper.mapToDomain as mapDetailsToDomain
import com.telda.movieApp.data.mapper.MovieMapper.mapToDomain as mapMovieToDomain

class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BaseRepo(), MovieRepository {

    /**
     * Fetch popular movies from the API
     * @param page Page number to fetch data for.
     * @return Flow emitting the state of the API call.
     */
    override suspend fun fetchPopularMovies(page: Int): Flow<DataState<List<Movie>>> {
        return performApiCall(apiCall = { apiService.fetchPopularMovies(page) }).map { dataState ->
            // Handle the success case and map the movie data
            when (dataState) {
                is DataState.Success -> {
                    val mappedMovies = dataState.data?.results
                        ?.filterNotNull() // Remove null items
                        ?.mapNotNull { it.mapMovieToDomain() } // Map to domain model
                        ?: emptyList() // Return empty list if no results
                    DataState.Success(mappedMovies)
                }

                else -> dataState as DataState<List<Movie>> // Return other states as-is
            }
        }
    }

    /**
     * Search for movies by query and page number.
     * @param query The search query.
     * @param page The page number to fetch.
     * @return Flow emitting the state of the search API call.
     */
    override suspend fun searchMovies(query: String, page: Int): Flow<DataState<List<Movie>>> {
        return performApiCall(apiCall = { apiService.searchMovies(query, page) }).map { dataState ->
            // Handle the success case and map the movie data
            when (dataState) {
                is DataState.Success -> {
                    val mappedMovies = dataState.data?.results
                        ?.filterNotNull() // Remove null items
                        ?.mapNotNull { it.mapMovieToDomain() } // Map to domain model
                        ?: emptyList()
                    DataState.Success(mappedMovies)
                }

                else -> dataState as DataState<List<Movie>> // Return other states as-is
            }
        }
    }

    /**
     * Fetch movie details by ID.
     * @param movieId The ID of the movie to fetch details for.
     * @return Flow emitting the state of the movie details API call.
     */
    override suspend fun fetchMovieDetailsById(movieId: Int): Flow<DataState<MovieDetails>> {
        return performApiCall(apiCall = { apiService.fetchMovieDetailsById(movieId) }).map { dataState ->
            // Handle the success case and map movie details
            when (dataState) {
                is DataState.Success -> {
                    val mappedDetails = dataState.data.mapDetailsToDomain()
                    if (mappedDetails != null) {
                        DataState.Success(mappedDetails) // Return mapped movie details
                    } else {
                        DataState.Error(CustomError(message = "Failed to map movie details."))
                    }
                }

                else -> dataState as DataState<MovieDetails> // Return other states as-is
            }
        }
    }

    /**
     * Fetch similar movies by movie ID.
     * @param movieId The ID of the movie to fetch similar movies for.
     * @return Flow emitting the state of the similar movies API call.
     */
    override suspend fun fetchSimilarMovies(movieId: Int): Flow<DataState<List<Movie>>> {
        return performApiCall(apiCall = { apiService.fetchSimilarMoviesById(movieId) }).map { dataState ->
            // Handle the success case and map the similar movie data
            when (dataState) {
                is DataState.Success -> {
                    val mappedMovies = dataState.data?.results
                        ?.filterNotNull() // Remove null items
                        ?.mapNotNull { it.mapMovieToDomain() } // Map to domain model
                        ?: emptyList()
                    DataState.Success(mappedMovies)
                }

                else -> dataState as DataState<List<Movie>> // Return other states as-is
            }
        }
    }

    /**
     * Fetch movie credits by movie ID.
     * @param movieId The ID of the movie to fetch credits for.
     * @return Flow emitting the state of the movie credits API call.
     */
    override suspend fun fetchMovieCreditsById(movieId: Int): Flow<DataState<MovieCredits>> {
        return performApiCall(apiCall = { apiService.fetchMovieCreditsById(movieId) }).map { dataState ->
            // Handle the success case and map movie credits
            when (dataState) {
                is DataState.Success -> {
                    val mappedCredits = dataState.data?.mapCreditsToDomain()
                    if (mappedCredits != null) {
                        DataState.Success(mappedCredits) // Return mapped movie credits
                    } else {
                        DataState.Error(CustomError("Failed to map movie credits"))
                    }
                }

                else -> dataState as DataState<MovieCredits> // Return other states as-is
            }
        }
    }
}




