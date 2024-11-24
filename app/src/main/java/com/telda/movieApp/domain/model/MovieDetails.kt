package com.telda.movieApp.domain.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val tagline: String,
    val releaseDate: String,
    val runtime: Int,
    val revenue: Int,
    val budget: Int,
    val voteAverage: Double,
    val posterPath: String?,
    val backdropPath: String?,
    val genres: List<Genre>,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<String>,
    val spokenLanguages: List<String>,
    val status: String,
    val homepage: String?,
    var watchlist:Boolean = false
)

data class Genre(
    val id: Int,
    val name: String
)

data class ProductionCompany(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
)
