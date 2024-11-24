import com.telda.movieApp.data.mapper.MovieCreditsMapper.mapToDomain
import com.telda.movieApp.data.model.CastItem
import com.telda.movieApp.data.model.CrewItem
import com.telda.movieApp.data.model.DepartmentTypes
import com.telda.movieApp.data.model.MovieCreditResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieCreditsMapperTest {

    @Test
    fun `mapToDomain with valid data returns correct MovieCredits`() {
        // Arrange
        val response = MovieCreditResponse(
            cast = listOf(
                CastItem(
                    id = 1,
                    name = "John Doe",
                    profilePath = "/path/to/profile.jpg",
                    popularity = "8.5",
                    knownForDepartment = "Acting",
                    character = "Hero"
                )
            ), crew = listOf(
                CrewItem(
                    id = 2,
                    name = "Jane Smith",
                    profilePath = "/path/to/crew.jpg",
                    popularity = "7.5",
                    department = "Directing",
                    job = "Director"
                )
            )
        )

        // Act
        val result = response.mapToDomain()


        // Assert
        with(result) {
            // Check cast
            assertEquals(1, cast.size)
            with(cast.first()) {
                assertEquals(1, id)
                assertEquals("John Doe", name)
                assertEquals("/path/to/profile.jpg", profilePath)
                assertEquals(8.5, popularity, 0.01)
                assertEquals(DepartmentTypes.ACTING, department)
                assertEquals("Hero", job)
            }

            // Check crew
            assertEquals(1, crew.size)
            with(crew.first()) {
                assertEquals(2, id)
                assertEquals("Jane Smith", name)
                assertEquals("/path/to/crew.jpg", profilePath)
                assertEquals(7.5, popularity, 0.01)
                assertEquals(DepartmentTypes.DIRECTING, department)
                assertEquals("Director", job)
            }
        }
    }

    @Test
    fun `mapToDomain with null lists returns empty lists`() {
        // Arrange
        val response = MovieCreditResponse(
            cast = null, crew = null
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        assertTrue(result.cast.isEmpty())
        assertTrue(result.crew.isEmpty())
    }

    @Test
    fun `mapToDomain with invalid popularity returns 0_0`() {
        // Arrange
        val response = MovieCreditResponse(
            cast = listOf(
                CastItem(
                    id = 1,
                    name = "John Doe",
                    profilePath = "/path.jpg",
                    popularity = "invalid",
                    knownForDepartment = "Acting",
                    character = "Hero"
                )
            ), crew = emptyList()
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        assertEquals(0.0, result.cast.first().popularity, 0.01)
    }

    @Test
    fun `mapToDomain filters out crew with invalid department`() {
        // Arrange
        val response = MovieCreditResponse(
            cast = emptyList(), crew = listOf(
                CrewItem(
                    id = 1,
                    name = "John Doe",
                    profilePath = "/path.jpg",
                    popularity = "7.5",
                    department = "InvalidDepartment",
                    job = "Something"
                )
            )
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        assertTrue(result.crew.isEmpty())
    }

    @Test
    fun `mapToDomain handles null items in lists`() {
        // Arrange
        val response = MovieCreditResponse(
            cast = listOf(
                null, CastItem(
                    id = 1,
                    name = "John Doe",
                    profilePath = "/path.jpg",
                    popularity = "7.5",
                    knownForDepartment = "Acting",
                    character = "Hero"
                ), null
            ), crew = listOf(
                null, CrewItem(
                    id = 2,
                    name = "Jane Smith",
                    profilePath = "/path.jpg",
                    popularity = "8.5",
                    department = "Director",
                    job = "Director"
                ), null
            )
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        assertEquals(1, result.cast.size)
        assertEquals(1, result.crew.size)
    }

    @Test
    fun `mapToDomain handles empty strings correctly`() {
        // Arrange
        val response = MovieCreditResponse(
            cast = listOf(
                CastItem(
                    id = 1,
                    name = "",
                    profilePath = "",
                    popularity = "",
                    knownForDepartment = "",
                    character = ""
                )
            ), crew = listOf(
                CrewItem(
                    id = 2, name = "", profilePath = "", popularity = "", department = "", job = ""
                )
            )
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        with(result.cast.first()) {
            assertEquals("", name)
            assertEquals("", profilePath ?: "")
            assertEquals(0.0, popularity, 0.01)
            assertEquals("", job)
        }

        // Crew with empty department should be filtered out
        assertTrue(result.crew.isEmpty())
    }

    @Test
    fun `mapToDomain handles null fields in Cast and Crew objects`() {
        // Arrange
        val response = MovieCreditResponse(
            cast = listOf(
                CastItem(
                    id = null,
                    name = null,
                    profilePath = null,
                    popularity = null,
                    knownForDepartment = null,
                    character = null
                )
            ), crew = listOf(
                CrewItem(
                    id = null,
                    name = null,
                    profilePath = null,
                    popularity = null,
                    department = null,
                    job = null
                )
            )
        )

        // Act
        val result = response.mapToDomain()

        // Assert
        with(result.cast.first()) {
            assertEquals(0, id)
            assertEquals("", name)
            assertNull(profilePath)
            assertEquals(0.0, popularity, 0.01)
            assertEquals("", job)
        }

        // Crew with null department should be filtered out
        assertTrue(result.crew.isEmpty())
    }
}