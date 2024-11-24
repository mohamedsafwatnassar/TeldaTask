package com.telda.movieApp.domain.usecase

import com.telda.movieApp.domain.model.GroupedMovies
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.domain.repository.MovieRepository
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
    private val markMoviesWithWatchlistStatusUseCase: MarkMoviesWithWatchlistStatusUseCase,
    private val ioDispatcher: CoroutineDispatcher // Inject dispatcher for better testing and flexibility,
) {

    // Pagination control
    private var currentPage = 1
    private var isLastPage = false
    private val allMovies = mutableListOf<Movie>() // Stores all movies fetched so far
    private var currentQuery = "" // Tracks the current search query

    /**
     * Executes the use case to search movies based on the provided query.
     * @param query The search query string.
     * @return Flow emitting the state of the grouped search results.
     */
    suspend operator fun invoke(query: String): Flow<DataState<GroupedMovies>> {

        // Reset pagination if the query changes
        if (currentQuery != query) {
            resetPagination()
            isLastPage = false
        }

        // Return idle state if there are no more pages to fetch
        if (isLastPage) return flowOf(DataState.Idle)

        // Update the current query
        currentQuery = query

        return repository.searchMovies(query, currentPage).map { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    val movies = dataState.data

                    val updatedMovies = markMoviesWithWatchlistStatusUseCase.markMoviesWithWatchlistStatus(movies)
                    allMovies.addAll(updatedMovies)

                    // Group the movies by release year
                    val groupedMovies = groupMoviesByYear(allMovies)

                    // Update pagination state
                    isLastPage = movies.isEmpty() // Mark the last page when no movies are returned
                    currentPage++ // Increment the page for the next fetch

                    // Return the grouped movies in a success state
                    DataState.Success(groupedMovies)
                }

                else -> dataState as DataState<GroupedMovies> // Return other states (Error, Loading, etc.) unchanged
            }
        }.flowOn(ioDispatcher)
    }

    /**
     * Resets the pagination state and clears the accumulated movie list.
     */
    fun resetPagination() {
        currentPage = 1
        isLastPage = false
        allMovies.clear() // Clear the accumulated movies
    }

    /**
     * Groups the list of movies by their release year, sorting them in descending order.
     * @param movies The list of movies to group and sort.
     * @return The grouped movies by year.
     */
    private fun groupMoviesByYear(movies: List<Movie>): GroupedMovies {
        return movies.groupBy { it.releaseYear }
            .filterKeys { it > 0 } // Exclude invalid years (0 or negative years)
            .toSortedMap(reverseOrder()) // Sort years in descending order
    }
}
