package com.telda.movieApp.data.mapper

import com.telda.movieApp.data.mapper.MovieMapper.mapResponseToDomain
import com.telda.movieApp.data.mapper.MovieMapper.mapToDomain
import com.telda.movieApp.data.model.MovieItem
import com.telda.movieApp.data.model.MoviesResponse
import com.telda.movieApp.domain.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieMapperTest {

    @Test
    fun `mapToDomain correctly maps MovieItem to Movie`() {
        // Arrange
        val movieItem = MovieItem(
            id = 1,
            title = "Test Movie",
            overview = "This is a test movie.",
            posterPath = "/test.jpg",
            releaseDate = "2024-11-23",
            popularity = 8.5
        )

        val expectedMovie = Movie(
            id = 1,
            title = "Test Movie",
            overview = "This is a test movie.",
            posterPath = "/test.jpg",
            releaseYear = 2024,
        )

        // Act
        val result = movieItem.mapToDomain()

        // Assert
        assertEquals(expectedMovie, result)
    }

    @Test
    fun `mapToDomain returns null for invalid MovieItem`() {
        // Arrange
        val movieItem = MovieItem(
            id = null, // Invalid ID
            title = "Invalid Movie",
            overview = "This should fail.",
            posterPath = null,
            releaseDate = "InvalidDate", // Invalid date
            popularity = "InvalidPopularity" // Invalid popularity
        )

        // Act
        val result = movieItem.mapToDomain()

        // Assert
        assertEquals(null, result)
    }

    @Test
    fun `mapToDomain handles missing release year gracefully`() {
        // Arrange
        val movieItem = MovieItem(
            id = 2,
            title = "No Release Year",
            overview = "Missing year in releaseDate.",
            posterPath = null,
            releaseDate = null, // Missing release date
            popularity = 7.2
        )

        val expectedMovie = Movie(
            id = 2,
            title = "No Release Year",
            overview = "Missing year in releaseDate.",
            posterPath = null,
            releaseYear = 0, // Defaulted to 0
        )

        // Act
        val result = movieItem.mapToDomain()

        // Assert
        assertEquals(expectedMovie, result)
    }

    @Test
    fun `mapResponseToDomain correctly maps MoviesResponse to List of Movies`() {
        // Arrange
        val moviesResponse = MoviesResponse(
            results = listOf(
                MovieItem(
                    id = 1,
                    title = "Movie 1",
                    overview = "Overview 1",
                    posterPath = "/poster1.jpg",
                    releaseDate = "2023-05-15",
                ),
                MovieItem(
                    id = 2,
                    title = "Movie 2",
                    overview = "Overview 2",
                    posterPath = "/poster2.jpg",
                    releaseDate = "2022-10-01",
                ),
                null // Null item to ensure it's ignored
            )
        )

        val expectedMovies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                posterPath = "/poster1.jpg",
                releaseYear = 2023,
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                overview = "Overview 2",
                posterPath = "/poster2.jpg",
                releaseYear = 2022,
            )
        )

        // Act
        val result = moviesResponse.mapResponseToDomain()

        // Assert
        assertEquals(expectedMovies, result)
    }

    @Test
    fun `mapResponseToDomain returns empty list for empty MoviesResponse`() {
        // Arrange
        val moviesResponse = MoviesResponse(results = emptyList())

        // Act
        val result = moviesResponse.mapResponseToDomain()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `mapResponseToDomain returns empty list for null MoviesResponse`() {
        // Arrange
        val moviesResponse = MoviesResponse(results = null)

        // Act
        val result = moviesResponse.mapResponseToDomain()

        // Assert
        assertTrue(result.isEmpty())
    }
}
