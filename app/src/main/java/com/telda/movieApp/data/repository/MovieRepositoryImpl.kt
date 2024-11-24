package com.telda.movieApp.data.repository

import com.telda.movieApp.data.api.ApiService
import com.telda.movieApp.data.common.BaseRepo
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.domain.repository.MovieRepository
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
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
}




