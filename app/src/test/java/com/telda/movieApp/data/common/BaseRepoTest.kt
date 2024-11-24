package com.telda.movieApp.data.common

import com.telda.movieApp.util.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class BaseRepoTest {

    private lateinit var baseRepo: BaseRepo

    @Before
    fun setUp() {
        baseRepo = object : BaseRepo() {} // Create an instance of the abstract class
    }

    @Test
    fun `performApiCall emits Success when response is successful`() = runBlocking {
        // Mock the successful response
        val mockResponse = Response.success("Success data")
        val mockApiCall: suspend () -> Response<String?> = { mockResponse }

        // Collect the emitted flow
        val emissions = baseRepo.performApiCall(mockApiCall).toList()

        // Verify the emitted values
        assertEquals(
            listOf(
                DataState.Processing,
                DataState.Idle,
                DataState.Success("Success data")
            ), emissions
        )
    }

    @Test
    fun `performApiCall emits ServerError when response code is 500`() = runBlocking {
        // Mock the 500 error response
        val mockResponse: Response<String?> = Response.error(500, mock())

        val mockApiCall: suspend () -> Response<String?> = { mockResponse }

        val emissions = baseRepo.performApiCall(mockApiCall).toList()

        assertEquals(
            listOf(DataState.Processing, DataState.Idle, DataState.ServerError),
            emissions
        )
    }


    @Test
    fun `performApiCall emits ServerError when exception is thrown`() = runBlocking {
        val mockApiCall: suspend () -> Response<String?> = { throw Exception("Network error") }

        val emissions = baseRepo.performApiCall(mockApiCall).toList()

        assertEquals(
            listOf(DataState.Processing, DataState.Idle, DataState.ServerError),
            emissions
        )
    }
}
