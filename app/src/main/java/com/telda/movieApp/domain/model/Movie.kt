package com.telda.movieApp.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseYear: Int,
    var watchlist: Boolean = false
)

typealias GroupedMovies = Map<Int, List<Movie>>