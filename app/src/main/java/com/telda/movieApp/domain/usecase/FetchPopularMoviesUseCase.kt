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

class FetchPopularMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
    private val markMoviesWithWatchlistStatusUseCase: MarkMoviesWithWatchlistStatusUseCase,
    private val ioDispatcher: CoroutineDispatcher // Inject dispatcher for better testing and flexibility,
) {

    // Pagination control
    private var currentPage = 1
    private var isLastPage = false
    private val allMovies = mutableListOf<Movie>() // Stores all movies fetched so far

    /**
     * Executes the use case to fetch and group popular movies by year.
     * @return Flow emitting the state of grouped popular movies.
     */
    suspend operator fun invoke(): Flow<DataState<GroupedMovies>> {
        // Return idle state if there are no more pages to fetch
        if (isLastPage) return flowOf(DataState.Idle)

        return repository.fetchPopularMovies(currentPage)
            .map { dataState ->
                handleMoviesDataState(dataState)
            }
            .flowOn(ioDispatcher)
    }

    /**
     * Handles the DataState of the fetched movies.
     * Updates pagination state and groups the movies.
     * @param dataState The DataState of the fetched movies.
     * @return The updated DataState with grouped movies.
     */
    private suspend fun handleMoviesDataState(dataState: DataState<List<Movie>>): DataState<GroupedMovies> {
        return when (dataState) {
            is DataState.Success -> {
                val movies = dataState.data

                val updatedMovies =
                    markMoviesWithWatchlistStatusUseCase.markMoviesWithWatchlistStatus(movies)
                allMovies.addAll(updatedMovies)

                // Group movies by year and return success state
                val groupedMovies = groupMoviesByYear(allMovies)
                isLastPage = movies.isEmpty() // Mark last page if no more movies
                currentPage++ // Increment the page for the next fetch

                DataState.Success(groupedMovies)
            }

            is DataState.Error -> {
                // Return error state
                DataState.Error(dataState.error)
            }

            DataState.Idle -> {
                // Return idle state when no movies left
                DataState.Idle
            }

            DataState.Processing -> {
                // Return processing state
                DataState.Processing
            }

            DataState.ServerError -> {
                // Return server error state
                DataState.ServerError
            }
        }
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

    /**
     * Resets the pagination state for the next request.
     */
    fun resetPagination() {
        currentPage = 1
        isLastPage = false
        allMovies.clear() // Clear the accumulated movies
    }
}
