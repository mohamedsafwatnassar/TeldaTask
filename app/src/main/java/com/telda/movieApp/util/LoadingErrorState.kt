package com.telda.movieApp.util

import com.telda.movieApp.domain.model.CustomError


sealed class LoadingErrorState {
    data class ShowError(val error: CustomError) : LoadingErrorState()
    data object ShowNetworkError : LoadingErrorState()
    data object ShowLoading: LoadingErrorState()
    data object HideLoading: LoadingErrorState()
}