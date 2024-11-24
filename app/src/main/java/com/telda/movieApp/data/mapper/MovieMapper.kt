package com.telda.movieApp.data.mapper

import com.telda.movieApp.data.model.MovieItem
import com.telda.movieApp.data.model.MoviesResponse
import com.telda.movieApp.domain.model.Movie

object MovieMapper {

    fun MovieItem.mapToDomain(): Movie? {
        return try {
            Movie(
                id = this.id ?: return null,
                title = this.title.orEmpty(),
                overview = this.overview.orEmpty(),
                posterPath = this.posterPath,
                releaseYear = this.releaseDate?.substring(0, 4)?.toIntOrNull() ?: 0,
            )
        } catch (e: Exception) {
            null // Return null if mapping fails
        }
    }

    fun MoviesResponse.mapResponseToDomain(): List<Movie> {
        return this?.results?.mapNotNull { movieItem ->
            movieItem?.let { it.mapToDomain() }
        } ?: emptyList()
    }
}