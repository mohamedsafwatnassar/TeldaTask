package com.telda.movieApp.data.mapper

import com.telda.movieApp.data.mapper.MovieDetailsMapper.mapToDomain
import com.telda.movieApp.data.model.MovieDetailsResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieDetailsMapperTest {

    @Test
    fun `mapToDomain with null MovieDetailsResponse returns null`() {
        val response: MovieDetailsResponse? = null
        assertNull(response.mapToDomain())
    }

    @Test
    fun `mapToDomain with null id returns null`() {
        val response = MovieDetailsResponse(id = null)
        assertNull(response.mapToDomain())
    }

    @Test
    fun `mapToDomain with valid data returns correct MovieDetails`() {
        // Arrange
        val response = MovieDetailsResponse(
            id = 1,
            title = "Test Movie",
            overview = "Test Overview",
            tagline = "Test Tagline",
            releaseDate = "2024-01-01",
            posterPath = "/poster.jpg",
            status = "Released",
            homepage = "https://movie.com"
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        requireNotNull(result)
        with(result) {
            assertEquals(1, id)
            assertEquals("Test Movie", title)
            assertEquals("Test Overview", overview)
            assertEquals("Test Tagline", tagline)
            assertEquals("2024-01-01", releaseDate)
            assertEquals("/poster.jpg", posterPath)
            assertEquals("Released", status)
        }
    }

    @Test
    fun `mapToDomain handles empty or null strings correctly`() {
        // Arrange
        val response = MovieDetailsResponse(
            id = 1,
            title = null,
            overview = "",
            tagline = null,
            releaseDate = "",
            posterPath = null,
            status = null,
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        requireNotNull(result)
        with(result) {
            assertEquals(1, id)
            assertEquals("", title)
            assertEquals("", overview)
            assertEquals("", tagline)
            assertEquals("", releaseDate)
            assertNull(posterPath)
            assertEquals("", status)
        }
    }

    @Test
    fun `mapToDomain handles null items in lists correctly`() {
        // Arrange
        val response = MovieDetailsResponse(
            id = 1,
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        requireNotNull(result)
    }
}