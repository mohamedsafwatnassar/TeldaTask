package com.telda.movieApp.util

import com.telda.movieApp.domain.model.CustomError

sealed class DataState<out T> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val error: CustomError) : DataState<Nothing>()
    data object ServerError : DataState<Nothing>()
    data object Processing: DataState<Nothing>()
    data object Idle: DataState<Nothing>()
}