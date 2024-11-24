package com.telda.movieApp.data.common

import com.telda.movieApp.domain.model.CustomError
import com.telda.movieApp.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

abstract class BaseRepo {
    inline fun <T> performApiCall(
        crossinline apiCall: suspend () -> Response<T?>,
    ): Flow<DataState<T?>> = flow {
        try {
            // Emit "Processing" state first
            emit(DataState.Processing)

            val response = apiCall()

            if (response.isSuccessful) {
                // Emit "Success" state when the response is successful
                emit(DataState.Success(response.body()))
                // Emit "Idle" state after "Success"
                emit(DataState.Idle)
            } else {
                // Handle server errors or other status codes
                when (response.code()) {
                    500 -> emit(DataState.ServerError)
                    else -> {
                        val errorBody = response.errorBody()?.string().toString()
                        emit(DataState.Error(CustomError(errorBody, response.code())))
                    }
                }
                // Emit "Idle" after the error
                emit(DataState.Idle)
            }
        } catch (e: Exception) {
            // Catch exceptions and emit appropriate error states
            println(e)
            emit(DataState.Idle)
            emit(DataState.ServerError)
        }
    }
}