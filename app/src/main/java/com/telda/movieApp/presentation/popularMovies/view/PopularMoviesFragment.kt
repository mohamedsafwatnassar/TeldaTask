package com.telda.movieApp.presentation.popularMovies.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.telda.android_task.presentation.base.BaseFragment
import com.telda.movieApp.databinding.FragmentPopularMoviesBinding
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.presentation.popularMovies.adapter.GroupedMoviesAdapter
import com.telda.movieApp.presentation.popularMovies.viewmodel.MoviesViewModel
import com.telda.movieApp.util.LoadingErrorState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PopularMoviesFragment : BaseFragment() {

    // View binding reference to interact with views in the fragment
    private var _binding: FragmentPopularMoviesBinding? = null
    private val binding get() = _binding!!

    // ViewModels to interact with data for movies, similar movies, and movie details
    private val moviesViewModel: MoviesViewModel by viewModels()

    // Adapter for displaying movies in a grouped format
    private lateinit var groupedMoviesAdapter: GroupedMoviesAdapter

    // Job to manage search debouncing and canceling previous search operations
    private var searchJob: Job? = null

    // Flag to check if the current search is from the search text input
    private var isFromSearch = false

    // Inflating the fragment's layout and binding the views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    // This function is called when the fragment's view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()  // Set up observers to listen to data changes
        setupListeners()  // Set up listeners for user interactions (like search input)
        initializeRecyclerView()  // Set up RecyclerView for displaying movies
        fetchInitialMovies()  // Fetch the initial set of popular movies
    }

    // Initialize RecyclerView and set up the adapter with item click handling
    private fun initializeRecyclerView() {
        groupedMoviesAdapter = GroupedMoviesAdapter(
            ::handleOnMovieClick,
            ::handleOnWishListMovieClick
        )  // Create adapter

        binding.rcvw.apply {
            adapter = groupedMoviesAdapter  // Set the adapter to the RecyclerView
            addOnScrollListener(createScrollListener())  // Add scroll listener for pagination
        }
    }

    // Fetch the initial list of popular movies when the fragment is created
    private fun fetchInitialMovies() {
        moviesViewModel.fetchPopularMovies()  // Call ViewModel to fetch popular movies
    }

    // Handle the movie click, which toggles the movie's watchlist status
    private fun handleOnMovieClick(movie: Movie) {
        val bundle = Bundle()
        bundle.putInt("movie_id", movie.id)
        // findNavController().customNavigate(R.id.mo)
    }

    // Handle the movie click, which toggles the movie's watchlist status
    private fun handleOnWishListMovieClick(movie: Movie, pos: Int) {
        // If the movie is already in the watchlist, remove it; otherwise, add it
        if (movie.watchlist) {
            moviesViewModel.removeMovieFromWatchList(movie.id) {
                movie.watchlist = false  // Update the movie status to not in watchlist
                showToast("Removed from watchlist")  // Show feedback to the user
                groupedMoviesAdapter.notifyItemChanged(pos)  // Notify adapter of change
            }
        } else {
            moviesViewModel.addMovieToWatchList(movie.id) {
                movie.watchlist = true  // Update the movie status to in watchlist
                showToast("Added to watchlist")  // Show feedback to the user
                groupedMoviesAdapter.notifyItemChanged(pos)  // Notify adapter of change
            }
        }
    }

    // Set up the listener for the search input field to trigger search functionality
    private fun setupListeners() {
        binding.search.doAfterTextChanged { text ->
            searchJob?.cancel()  // Cancel any ongoing search job
            searchJob = lifecycleScope.launch {
                delay(300)  // Add delay to debounce the input
                handleSearchQuery(text)  // Handle the search query input
            }
        }
    }

    // Handle search query logic; fetch either searched movies or popular movies based on input
    private fun handleSearchQuery(text: CharSequence?) {
        isFromSearch = !text.isNullOrEmpty()  // Determine if the search is from user input
        if (isFromSearch) {
            moviesViewModel.searchMovies(text.toString())  // Fetch movies based on search query
        } else {
            moviesViewModel.fetchPopularMovies()  // Fetch popular movies when no search query
        }
    }

    // Create a listener for RecyclerView scroll events (used for pagination)
    private fun createScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleRecyclerViewScroll(recyclerView)  // Handle scroll logic
            }
        }
    }

    // Handle scroll events to detect when to load more items (for pagination)
    private fun handleRecyclerViewScroll(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        // If we've scrolled to the bottom of the list, load more movies
        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
            fetchMoviesBasedOnSearch()  // Fetch more movies based on search status
        }
    }

    // Fetch movies based on whether the user is searching or viewing popular movies
    private fun fetchMoviesBasedOnSearch() {
        if (isFromSearch) {
            moviesViewModel.searchMovies(binding.search.text.toString())  // Continue search if active
        } else {
            moviesViewModel.fetchPopularMovies()  // Load more popular movies
        }
    }

    // Set up the observers to listen for changes in data from the ViewModel
    private fun setupObservers() {
        moviesViewModel.viewState.observe(
            viewLifecycleOwner,
            ::handleViewState
        )  // Observe loading/error states
        moviesViewModel.moviesByYear.observe(viewLifecycleOwner) {
            groupedMoviesAdapter.updateGroupedMovies(it)  // Update the adapter with new movies
        }
    }

    // Handle the view state, showing loading, error, or network error messages
    private fun handleViewState(state: LoadingErrorState) {
        when (state) {
            LoadingErrorState.ShowLoading -> showLoading()  // Show loading spinner
            LoadingErrorState.HideLoading -> hideLoading()  // Hide loading spinner
            is LoadingErrorState.ShowError -> showToast(state.error.message)  // Show error message
            is LoadingErrorState.ShowNetworkError -> showToast("Network error")  // Show network error message
        }
    }

    // Clean up resources when the fragment is destroyed, including canceling the search job
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clear the binding reference
        searchJob?.cancel()  // Cancel any ongoing search job to avoid memory leaks
    }
}