package com.telda.movieApp.presentation.popularMovies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.telda.movieApp.domain.model.CustomError
import com.telda.movieApp.domain.model.GroupedMovies
import com.telda.movieApp.domain.usecase.AddRemoveWatchListUseCase
import com.telda.movieApp.domain.usecase.FetchPopularMoviesUseCase
import com.telda.movieApp.domain.usecase.MarkMoviesWithWatchlistStatusUseCase
import com.telda.movieApp.domain.usecase.SearchMoviesUseCase
import com.telda.movieApp.presentation.base.BaseViewModel
import com.telda.movieApp.util.DataState
import com.telda.movieApp.util.LoadingErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val fetchPopularMoviesUseCase: FetchPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val markMoviesWithWatchlistStatusUseCase: MarkMoviesWithWatchlistStatusUseCase,
    private val addRemoveWatchListUseCase: AddRemoveWatchListUseCase
) : BaseViewModel() {

    // LiveData to hold the grouped movies by year (used for observing data in the UI)
    private val _moviesByYear = MutableLiveData<GroupedMovies>()
    val moviesByYear: LiveData<GroupedMovies> = _moviesByYear

    // Job to manage ongoing network requests (cancel previous jobs when starting new ones)
    private var job: Job? = null

    /**
     * Fetches popular movies and groups them by release year.
     * Cancels any ongoing job before starting a new one.
     */
    fun fetchPopularMovies() {
        // Cancel any ongoing job before starting a new one
        job?.cancel()
        job = viewModelScope.launch {
            // Collect the result from the use case
            fetchPopularMoviesUseCase().collect { dataState ->
                handleDataState(dataState)  // Handle the result based on the data state
            }
        }
    }

    /**
     * Searches for movies based on the provided query and groups them by release year.
     * Cancels any ongoing job before starting a new one.
     * @param query The search query entered by the user.
     */
    fun searchMovies(query: String) {
        // Cancel any ongoing job before starting a new one
        job?.cancel()
        job = viewModelScope.launch {
            // Collect the result from the use case
            searchMoviesUseCase(query).collect { dataState ->
                handleDataState(dataState)  // Handle the result based on the data state
            }
        }
    }

    /**
     * Handles the different data states (Success, Error, Idle, Processing, ServerError).
     * Updates the UI accordingly.
     * @param dataState The current state of the data (Success, Error, etc.).
     */
    private fun handleDataState(dataState: DataState<GroupedMovies>) {
        when (dataState) {
            is DataState.Success -> {
                // On success, update the LiveData with the grouped movies data
                _moviesByYear.postValue(dataState.data)
            }

            is DataState.Error -> {
                // Handle error state, post the error message to _viewState
                _viewState.postValue(LoadingErrorState.ShowError(dataState.error))
            }

            DataState.Idle -> {
                // Hide loading state when idle (no data loading in progress)
                _viewState.postValue(LoadingErrorState.HideLoading)
            }

            DataState.Processing -> {
                // Show loading state when data is being processed
                _viewState.postValue(LoadingErrorState.ShowLoading)
            }

            DataState.ServerError -> {
                // Handle server error state if necessary (show a network error)
                _viewState.postValue(LoadingErrorState.ShowNetworkError)
            }
        }
    }

    /**
     * Adds a movie to the watchlist.
     * @param movieId The ID of the movie to be added.
     * @param successCallback Callback function to be invoked upon success.
     */
    fun addMovieToWatchList(movieId: Int, successCallback: () -> Unit) {
        viewModelScope.launch {
            try {
                addRemoveWatchListUseCase.addMovieToWatchList(movieId)  // Add the movie to watchlist
                successCallback.invoke()  // Trigger success callback after successful operation
            } catch (e: Exception) {
                // Handle any exceptions and post the error message to _viewState
                _viewState.postValue(LoadingErrorState.ShowError(CustomError(e.localizedMessage.toString())))
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
     * This ensures that no background tasks continue after the ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        job?.cancel()  // Cancel any ongoing job to prevent memory leaks and ensure proper cleanup
    }
}

