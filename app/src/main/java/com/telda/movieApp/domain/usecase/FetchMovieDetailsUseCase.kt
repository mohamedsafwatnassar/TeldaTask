package com.telda.movieApp.domain.usecase

import com.telda.movieApp.domain.model.MovieDetails
import com.telda.movieApp.domain.repository.MovieRepository
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository,
    private val markMoviesWithWatchlistStatusUseCase: MarkMoviesWithWatchlistStatusUseCase,
    private val ioDispatcher: CoroutineDispatcher // Inject dispatcher for better testing and flexibility,
) {

    /**
     * Executes the use case to fetch movie details by ID.
     * @param movieId The ID of the movie to fetch details for.
     * @return Flow emitting the state of the movie details.
     */
    suspend operator fun invoke(movieId: Int): Flow<DataState<MovieDetails>> {
        return repository.fetchMovieDetailsById(movieId).map { dataState ->
            // Handle the different states returned from the repository
            when (dataState) {
                is DataState.Success -> {
                    val movie =
                        markMoviesWithWatchlistStatusUseCase.markMovieWithWatchlistStatus(dataState.data)

                    // Return the fetched movie details in a success state
                    DataState.Success(movie)
                }

                else -> dataState // Return other states (Error, Loading, etc.) as they are
            }
        }.flowOn(ioDispatcher)
    }
}
