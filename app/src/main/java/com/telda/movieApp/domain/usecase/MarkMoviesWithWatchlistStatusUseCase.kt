package com.telda.movieApp.domain.usecase

import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.domain.repository.LocalMovieRepository
import javax.inject.Inject

class MarkMoviesWithWatchlistStatusUseCase @Inject constructor(
    private val localRepository: LocalMovieRepository,
) {
    suspend fun markMoviesWithWatchlistStatus(movies: List<Movie>): List<Movie> {
        val watchlistMovieIds = localRepository.fetchWatchlistMoviesFromLocal().map { it.id }
        return movies.map { movie ->
            movie.apply {
                watchlist = watchlistMovieIds.contains(id)
            }
        }
    }

}