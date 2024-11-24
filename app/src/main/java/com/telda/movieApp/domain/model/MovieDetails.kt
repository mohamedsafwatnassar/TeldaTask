package com.telda.movieApp.domain.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val tagline: String,
    val releaseDate: String,
    val posterPath: String?,
    val status: String,
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
