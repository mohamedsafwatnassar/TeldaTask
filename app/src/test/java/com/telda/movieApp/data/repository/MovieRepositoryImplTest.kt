package com.telda.movieApp.data.repository

import com.telda.movieApp.data.api.ApiService
import com.telda.movieApp.data.model.MovieItem
import com.telda.movieApp.data.model.MoviesResponse
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class MovieRepositoryImplTest {
    private lateinit var movieRepository: MovieRepositoryImpl
    private lateinit var apiService: ApiService
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        apiService = mock()
        movieRepository = MovieRepositoryImpl(apiService)
    }

    @Test
    fun `fetchPopularMovies returns success when API call succeeds`() = runTest {
        // Arrange
        val moviesResponse = MoviesResponse(
            results = listOf(
                MovieItem(
                    id = 1,
                    title = "Movie 1",
                    overview = "Overview",
                    releaseDate = "2024-11-23",
                ),
                MovieItem(
                    id = 2,
                    title = "Movie 2",
                    overview = "Overview",
                    releaseDate = "2024-11-23",
                )
            )
        )
        whenever(apiService.fetchPopularMovies(1)).thenReturn(Response.success(moviesResponse))

        // Act
        val result: Flow<DataState<List<Movie>>> = movieRepository.fetchPopularMovies(1)

        // Assert
        result.collect { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    assert(dataState.data.size == 2)
                    assert(dataState.data.first().title == "Movie 1")
                }

                is DataState.Idle -> {}
                is DataState.Processing -> {}
                is DataState.ServerError -> {}
                is DataState.Error -> {}
            }
        }
    }
}
