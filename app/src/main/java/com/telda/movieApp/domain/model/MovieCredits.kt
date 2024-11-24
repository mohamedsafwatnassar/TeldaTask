package com.telda.movieApp.domain.model

import com.telda.movieApp.data.model.DepartmentTypes

data class MovieCredits(
    val cast: List<MoviePerson>,
    val crew: List<MoviePerson>
)

data class MoviePerson(
    val id: Int,
    val name: String,
    val profilePath: String?,
    val popularity: Double,
    val department: DepartmentTypes?,
    val job: String
)
