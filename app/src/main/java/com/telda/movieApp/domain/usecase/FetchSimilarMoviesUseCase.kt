package com.telda.movieApp.domain.usecase

import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.domain.repository.MovieRepository
import com.telda.movieApp.domain.usecase.MarkMoviesWithWatchlistStatusUseCase
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchSimilarMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
    private val markMoviesWithWatchlistStatusUseCase: MarkMoviesWithWatchlistStatusUseCase,
    private val ioDispatcher: CoroutineDispatcher // Inject dispatcher for better testing and flexibility,
) {

    /**
     * Executes the use case to fetch similar movies based on a given movie ID.
     * @param movieId The ID of the movie for which similar movies are to be fetched.
     * @return Flow emitting the state of the list of similar movies.
     */
    suspend operator fun invoke(movieId: Int): Flow<DataState<List<Movie>>> {
        return repository.fetchSimilarMovies(movieId).map { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    // Take only the top 5 similar movies
                    val movies = dataState.data
                    val updatedMovies =
                        markMoviesWithWatchlistStatusUseCase.markMoviesWithWatchlistStatus(movies)
                    DataState.Success(updatedMovies.take(5)) // Return the top 5 similar movies
                }

                else -> dataState // Pass other states unchanged (Error, Loading, etc.)
            }
        }.flowOn(ioDispatcher)
    }
}
