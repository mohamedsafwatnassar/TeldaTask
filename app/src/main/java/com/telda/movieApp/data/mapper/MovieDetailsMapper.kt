package com.telda.movieApp.data.mapper

import com.telda.movieApp.data.model.GenresItem
import com.telda.movieApp.data.model.MovieDetailsResponse
import com.telda.movieApp.data.model.ProductionCompaniesItem
import com.telda.movieApp.domain.model.Genre
import com.telda.movieApp.domain.model.MovieDetails
import com.telda.movieApp.domain.model.ProductionCompany

object MovieDetailsMapper {

    fun MovieDetailsResponse?.mapToDomain(): MovieDetails? {
        if (this == null) return null

        return MovieDetails(
            id = this.id ?: return null,
            title = this.title.orEmpty(),
            overview = this.overview.orEmpty(),
            tagline = this.tagline.orEmpty(),
            releaseDate = this.releaseDate.orEmpty(),
            posterPath = this.posterPath,
            status = this.status.orEmpty(),
        )
    }

    fun GenresItem.toDomain(): Genre? {
        return id?.let { Genre(id = it, name = name.orEmpty()) }
    }

    fun ProductionCompaniesItem.toDomain(): ProductionCompany? {
        return id?.let {
            ProductionCompany(
                id = it,
                name = name.orEmpty(),
                logoPath = logoPath,
                originCountry = originCountry.orEmpty()
            )
        }
    }
}
