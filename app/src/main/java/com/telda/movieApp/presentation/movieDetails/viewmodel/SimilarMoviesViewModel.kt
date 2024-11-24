package com.telda.movieApp.presentation.movieDetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.telda.movieApp.data.model.DepartmentTypes
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.domain.model.MoviePerson
import com.telda.movieApp.domain.usecase.FetchMovieCreditsUseCase
import com.telda.movieApp.domain.usecase.FetchSimilarMoviesUseCase
import com.telda.movieApp.presentation.base.BaseViewModel
import com.telda.movieApp.util.DataState
import com.telda.movieApp.util.LoadingErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimilarMoviesViewModel @Inject constructor(
    private val fetchSimilarMoviesUseCase: FetchSimilarMoviesUseCase,
    private val fetchMovieCreditsUseCase: FetchMovieCreditsUseCase
) : BaseViewModel() {

    // LiveData for similar movies
    private val _similarMovies = MutableLiveData<List<Movie>>()
    val similarMovies: LiveData<List<Movie>> = _similarMovies

    // LiveData for movie credits
    private val _movieCredits = MutableLiveData<Map<DepartmentTypes, List<MoviePerson>>>()
    val movieCredits: LiveData<Map<DepartmentTypes, List<MoviePerson>>> = _movieCredits

    // Job to manage ongoing network requests
    private var job: Job? = null

    /**
     * Fetches similar movies by movie ID.
     * Cancels any ongoing job before starting a new one.
     * @param movieId The ID of the movie to fetch similar movies for.
     */
    fun fetchSimilarMoviesById(movieId: Int) {
        // Cancel any ongoing job before starting a new one
        job?.cancel()
        job = viewModelScope.launch {
            fetchSimilarMoviesUseCase(movieId).collect { dataState ->
                handleDataStateForMovies(dataState)
            }
        }
    }

    /**
     * Fetches movie credits by movie ID.
     * Cancels any ongoing job before starting a new one.
     * @param movieId The ID of the movie to fetch credits for.
     */
    fun fetchMovieCreditsById(movieId: Int) {
        // Cancel any ongoing job before starting a new one
        job?.cancel()
        job = viewModelScope.launch {
            fetchMovieCreditsUseCase(movieId).collect { dataState ->
                handleDataStateForCredits(dataState)
            }
        }
    }

    /**
     * Handles the data state for fetching similar movies.
     * @param dataState The current state of the data (Success, Error, etc.).
     */
    private fun handleDataStateForMovies(dataState: DataState<List<Movie>>) {
        when (dataState) {
            is DataState.Success -> {
                _similarMovies.postValue(dataState.data) // Update similar movies LiveData on success
            }

            is DataState.Error -> {
                // Handle error state if necessary
            }

            DataState.Idle -> {
                _viewState.postValue(LoadingErrorState.HideLoading) // Hide loading state
            }

            DataState.Processing -> {
                _viewState.postValue(LoadingErrorState.ShowLoading) // Show loading state
            }

            DataState.ServerError -> {
                // Handle server error state if necessary
            }
        }
    }

    /**
     * Handles the data state for fetching movie credits.
     * @param dataState The current state of the data (Success, Error, etc.).
     */
    private fun handleDataStateForCredits(dataState: DataState<Map<DepartmentTypes, List<MoviePerson>>>) {
        when (dataState) {
            is DataState.Success -> {
                _movieCredits.postValue(dataState.data) // Update movie credits LiveData on success
            }

            is DataState.Error -> {
                // Handle error state if necessary
            }

            DataState.Idle -> {
                _viewState.postValue(LoadingErrorState.HideLoading) // Hide loading state
            }

            DataState.Processing -> {
                _viewState.postValue(LoadingErrorState.ShowLoading) // Show loading state
            }

            DataState.ServerError -> {
                // Handle server error state if necessary
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
