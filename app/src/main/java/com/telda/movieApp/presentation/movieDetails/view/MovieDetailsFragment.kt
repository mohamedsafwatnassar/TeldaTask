package com.telda.movieApp.presentation.movieDetails.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.telda.android_task.presentation.base.BaseFragment
import com.telda.movieApp.R
import com.telda.movieApp.databinding.FragmentMovieDetailsBinding
import com.telda.movieApp.domain.model.MovieDetails
import com.telda.movieApp.presentation.movieDetails.adapter.MovieCreditAdapter
import com.telda.movieApp.presentation.movieDetails.adapter.SimilarMoviesAdapter
import com.telda.movieApp.presentation.movieDetails.viewmodel.MovieDetailsViewModel
import com.telda.movieApp.presentation.movieDetails.viewmodel.SimilarMoviesViewModel
import com.telda.movieApp.presentation.popularMovies.viewmodel.MoviesViewModel
import com.telda.movieApp.util.Constant
import com.telda.movieApp.util.LoadingErrorState
import com.telda.movieApp.util.extention.onDebouncedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : BaseFragment() {

    // Binding to access views in the layout
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    // ViewModels for managing data and business logic
    private val moviesViewModel: MoviesViewModel by viewModels()
    private val similarMoviesViewModel: SimilarMoviesViewModel by viewModels()
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()

    private var movieId: Int? = null  // ID of the selected movie
    private lateinit var movieDetails: MovieDetails  // Movie details to be displayed

    // Adapters for RecyclerViews
    private lateinit var similarMoviesAdapter: SimilarMoviesAdapter
    private lateinit var movieCreditAdapter: MovieCreditAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the movie ID passed via arguments
        movieId = requireArguments().getInt("movie_id")

        setupUI()           // Initialize UI components
        setupObservers()    // Attach observers for LiveData
        fetchInitialData()  // Fetch the necessary data
    }

    /**
     * Initialize UI components, listeners, and adapters.
     */
    private fun setupUI() {
        setupListeners()
        setupSimilarMoviesRecyclerView()
        setupMovieCreditRecyclerView()
    }

    /**
     * Set up click listeners for the watchlist functionality.
     */
    private fun setupListeners() {
        binding.watchlist.onDebouncedListener {
            toggleWatchlistStatus()
        }
    }

    /**
     * Fetch movie details and similar movies data using ViewModels.
     */
    private fun fetchInitialData() {
        movieId?.let {
            movieDetailsViewModel.fetchMovieDetailsById(it)
            similarMoviesViewModel.fetchSimilarMoviesById(it)
        }
    }

    /**
     * Attach observers to listen for LiveData changes.
     */
    private fun setupObservers() {
        // Observe movie details data
        movieDetailsViewModel.viewState.observe(viewLifecycleOwner, ::handleViewState)

        movieDetailsViewModel.movieDetails.observe(viewLifecycleOwner) {
            movieDetails = it
            displayMovieDetails()
        }

        // Observe similar movies data
        similarMoviesViewModel.similarMovies.observe(viewLifecycleOwner) {
            similarMoviesAdapter.updateSimilarMovies(it)
        }

        // Observe movie credits data
        similarMoviesViewModel.movieCredits.observe(viewLifecycleOwner) {
            movieCreditAdapter.submitList(it)
        }
    }

    /**
     * Display movie details in the UI.
     */
    private fun displayMovieDetails() {
        with(binding) {
            title.text = movieDetails.title
            status.text = movieDetails.status
            releaseDate.text = movieDetails.releaseDate
            tagline.text = movieDetails.tagline
            overview.text = movieDetails.overview

            val moviePosterUrl = Constant.BASE_IMAGE_URL + movieDetails.posterPath
            Glide.with(root.context)
                .load(moviePosterUrl)
                .into(imgMovie)

            watchlist.setImageResource(
                if (movieDetails.watchlist) R.drawable.ic_watch_list else R.drawable.ic_not_watch_list
            )
        }
    }

    /**
     * Handle adding or removing a movie from the watchlist.
     */
    private fun toggleWatchlistStatus() {
        movieId?.let { id ->
            if (movieDetails.watchlist) {
                moviesViewModel.removeMovieFromWatchList(id) {
                    movieDetails.watchlist = false
                    updateWatchlistIcon(false)
                    showToast("Removed from watchlist")
                }
            } else {
                moviesViewModel.addMovieToWatchList(id) {
                    movieDetails.watchlist = true
                    updateWatchlistIcon(true)
                    showToast("Added to watchlist")
                }
            }
        }
    }

    /**
     * Update the watchlist icon based on the movie's status.
     */
    private fun updateWatchlistIcon(isInWatchlist: Boolean) {
        binding.watchlist.setImageResource(
            if (isInWatchlist) R.drawable.ic_watch_list else R.drawable.ic_not_watch_list
        )
    }

    /**
     * Initialize the RecyclerView for similar movies.
     */
    private fun setupSimilarMoviesRecyclerView() {
        similarMoviesAdapter = SimilarMoviesAdapter(::handleSimilarMovieClick)
        binding.rvSimilarMovies.adapter = similarMoviesAdapter
    }

    /**
     * Initialize the RecyclerView for movie credits.
     */
    private fun setupMovieCreditRecyclerView() {
        movieCreditAdapter = MovieCreditAdapter()
        binding.rvMovieCredit.adapter = movieCreditAdapter
    }

    /**
     * Handle clicks on similar movies.
     */
    private fun handleSimilarMovieClick(movieId: Int) {
        similarMoviesViewModel.fetchMovieCreditsById(movieId)
    }

    /**
     * Handle various loading and error states in the UI.
     */
    private fun handleViewState(state: LoadingErrorState) {
        when (state) {
            LoadingErrorState.ShowLoading -> showLoading()
            LoadingErrorState.HideLoading -> hideLoading()
            is LoadingErrorState.ShowError -> showToast(state.error.message)
            is LoadingErrorState.ShowNetworkError -> showToast("Network error")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
