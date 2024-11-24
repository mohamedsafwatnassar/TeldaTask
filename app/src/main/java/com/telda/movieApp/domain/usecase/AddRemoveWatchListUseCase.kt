package com.telda.movieApp.domain.usecase

import com.telda.movieApp.data.local.entity.MovieEntity
import com.telda.movieApp.domain.repository.LocalMovieRepository
import javax.inject.Inject

class AddRemoveWatchListUseCase @Inject constructor(
    private val localRepository: LocalMovieRepository,
) {


    suspend fun addMovieToWatchList(movieId: Int) {
        localRepository.addMovieToWatchlist(MovieEntity(movieId))
    }

    suspend fun removeMovieFromWatchList(movieId: Int) {
        localRepository.removeMovieFromWatchlist(MovieEntity(movieId))
    }
}
