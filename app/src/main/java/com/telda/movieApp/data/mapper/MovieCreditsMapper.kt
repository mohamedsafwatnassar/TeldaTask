package com.telda.movieApp.data.mapper

import com.telda.movieApp.data.model.DepartmentTypes
import com.telda.movieApp.data.model.MovieCreditResponse
import com.telda.movieApp.domain.model.MovieCredits
import com.telda.movieApp.domain.model.MoviePerson

object MovieCreditsMapper {
    fun MovieCreditResponse.mapToDomain(): MovieCredits {
        return MovieCredits(
            cast = this.cast?.mapNotNull {
                it?.let { castItem ->
                    MoviePerson(
                        id = castItem.id ?: 0,
                        name = castItem.name.orEmpty(),
                        profilePath = castItem.profilePath,
                        popularity = castItem.popularity?.toString()?.toDoubleOrNull() ?: 0.0,
                        department = DepartmentTypes.fromValue(it.knownForDepartment), // Enum for Actors
                        job = castItem.character.orEmpty()
                    )
                }
            }.orEmpty(),
            crew = this.crew?.mapNotNull {
                it?.let { crewItem ->
                    val department = DepartmentTypes.fromValue(crewItem.job.orEmpty())
                    if (department != null) {
                        MoviePerson(
                            id = crewItem.id ?: 0,
                            name = crewItem.name.orEmpty(),
                            profilePath = crewItem.profilePath,
                            popularity = crewItem.popularity?.toString()?.toDoubleOrNull() ?: 0.0,
                            department = department, // Use enum mapping
                            job = crewItem.job.orEmpty()
                        )
                    } else null
                }
            }.orEmpty()
        )
    }
}
