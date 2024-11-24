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
            runtime = this.runtime ?: 0,
            revenue = this.revenue ?: 0,
            budget = this.budget ?: 0,
            voteAverage = (this.voteAverage as? Double) ?: 0.0,
            posterPath = this.posterPath,
            backdropPath = this.backdropPath,
            genres = this.genres?.mapNotNull { it?.toDomain() } ?: emptyList(),
            productionCompanies = this.productionCompanies?.mapNotNull { it?.toDomain() }
                ?: emptyList(),
            productionCountries = this.productionCountries?.mapNotNull { it?.name }.orEmpty(),
            spokenLanguages = this.spokenLanguages?.mapNotNull { it?.name }.orEmpty(),
            status = this.status.orEmpty(),
            homepage = this.homepage
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
