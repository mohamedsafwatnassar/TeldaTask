package com.telda.movieApp.presentation.movieDetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.telda.movieApp.domain.model.CustomError
import com.telda.movieApp.domain.model.MovieDetails
import com.telda.movieApp.domain.usecase.AddRemoveWatchListUseCase
import com.telda.movieApp.domain.usecase.FetchMovieDetailsUseCase
import com.telda.movieApp.presentation.base.BaseViewModel
import com.telda.movieApp.util.DataState
import com.telda.movieApp.util.LoadingErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val fetchMovieDetailsUseCase: FetchMovieDetailsUseCase,
    private val addRemoveWatchListUseCase: AddRemoveWatchListUseCase
) : BaseViewModel() {

    // LiveData for storing movie details
    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> = _movieDetails

    // To manage ongoing network request
    private var job: Job? = null

    /**
     * Fetches the movie details based on the provided movie ID.
     * Cancels any ongoing job before starting a new request.
     * @param movieId The ID of the movie whose details are to be fetched.
     */
    fun fetchMovieDetailsById(movieId: Int) {
        // Cancel any existing job before starting a new one
        job?.cancel()
        job = viewModelScope.launch {
            // Collect the result from the use case
            fetchMovieDetailsUseCase(movieId).collect { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        // On success, post the movie details to LiveData
                        _movieDetails.postValue(dataState.data)
                    }

                    is DataState.Error -> {
                        // Handle the error state, if necessary
                        _viewState.postValue(LoadingErrorState.ShowError(dataState.error))
                    }

                    DataState.Idle -> {
                        // Hide loading state when idle
                        _viewState.postValue(LoadingErrorState.HideLoading)
                    }

                    DataState.Processing -> {
                        // Show loading state when processing
                        _viewState.postValue(LoadingErrorState.ShowLoading)
                    }

                    DataState.ServerError -> {
                        // Handle server error state, if necessary
                        _viewState.postValue(LoadingErrorState.ShowNetworkError)

                    }
                }
            }
        }
    }


    /**
     * Removes a movie from the watchlist.
     * @param movieId The ID of the movie to be removed.
     * @param successCallback Callback function to be invoked upon success.
     */
    fun removeMovieFromWatchList(movieId: Int, successCallback: () -> Unit) {
        viewModelScope.launch {
            try {
                addRemoveWatchListUseCase.removeMovieFromWatchList(movieId)  // Remove the movie from watchlist
                successCallback.invoke()  // Trigger success callback after successful operation
            } catch (e: Exception) {
                // Handle any exceptions and post the error message to _viewState
                _viewState.postValue(LoadingErrorState.ShowError(CustomError(e.localizedMessage.toString())))
            }
        }
    }

    /**
     * Cancels the ongoing job when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        job?.cancel() // Ensure to cancel the job when ViewModel is cleared
    }
}
