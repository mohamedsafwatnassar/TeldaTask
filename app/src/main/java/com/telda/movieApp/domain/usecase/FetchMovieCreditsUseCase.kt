package com.telda.movieApp.domain.usecase

import com.telda.movieApp.data.model.DepartmentTypes
import com.telda.movieApp.domain.model.MoviePerson
import com.telda.movieApp.domain.repository.MovieRepository
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FetchMovieCreditsUseCase @Inject constructor(
    private val repository: MovieRepository,
    private val ioDispatcher: CoroutineDispatcher // Inject dispatcher for better testing and flexibility,
) {

    /**
     * Executes the use case to fetch and group movie credits.
     * @param movieId The ID of the movie whose credits are to be fetched.
     * @return Flow emitting the state of the movie credits, grouped by department (acting, directing).
     */
    suspend operator fun invoke(movieId: Int): Flow<DataState<Map<DepartmentTypes, List<MoviePerson>>>> {
        return repository.fetchMovieCreditsById(movieId).map { dataState ->
            // Handling different states returned from the repository
            when (dataState) {
                is DataState.Success -> {
                    val credits = dataState.data

                    // Grouping movie credits by department (ACTING and DIRECTING)
                    val groupedData = mapOf(
                        // Group and sort actors by popularity (top 5)
                        DepartmentTypes.ACTING to credits.cast
                            .filter { it.department == DepartmentTypes.ACTING }
                            .sortedByDescending { it.popularity }
                            .take(5),

                        // Group and sort directors by popularity (top 5)
                        DepartmentTypes.DIRECTING to credits.crew
                            .filter { it.department == DepartmentTypes.DIRECTING }
                            .sortedByDescending { it.popularity }
                            .take(5)
                    )

                    // Return grouped data in a success state
                    DataState.Success(groupedData)
                }

                else -> dataState as DataState<Map<DepartmentTypes, List<MoviePerson>>> // Pass other states unchanged
            }
        }.flowOn(ioDispatcher)
    }
}
