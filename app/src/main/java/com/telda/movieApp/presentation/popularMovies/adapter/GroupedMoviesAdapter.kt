package com.telda.movieApp.presentation.popularMovies.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.telda.movieApp.R
import com.telda.movieApp.databinding.ItemFormFieldBinding
import com.telda.movieApp.databinding.ItemYearFieldBinding
import com.telda.movieApp.domain.model.GroupedMovies
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.util.Constant

class GroupedMoviesAdapter(
    private val onMovieClick: (movie: Movie) -> Unit,
    private val onWishListMovieClick: (movie: Movie, position: Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var groupedMovies: GroupedMovies = emptyMap()

    // View types for headers and items
    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    private val flattenedList = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemYearFieldBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                YearHeaderViewHolder(binding)
            }

            VIEW_TYPE_ITEM -> {
                val binding = ItemFormFieldBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MovieViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is YearHeaderViewHolder -> holder.bind(flattenedList[position] as Int)
            is MovieViewHolder -> holder.bind(flattenedList[position] as Movie)
        }
    }

    override fun getItemCount(): Int = flattenedList.size

    override fun getItemViewType(position: Int): Int {
        return if (flattenedList[position] is Int) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    /**
     * ViewHolder for year headers.
     */
    inner class YearHeaderViewHolder(private val binding: ItemYearFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(year: Int) {
            binding.txtYear.text = year.toString()
        }
    }

    /**
     * ViewHolder for movie items.
     */
    inner class MovieViewHolder(private val binding: ItemFormFieldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.title.text = movie.title
            binding.overview.text = movie.overview
            // binding.releaseYear.text = movie.releaseYear.toString()
            // imageUrl =  Constant.BASE_IMAGE_URL + movie.posterPath

            val moviePosterUrl = Constant.BASE_IMAGE_URL + movie.posterPath

            Glide.with(binding.root.context)
                .load(moviePosterUrl)
                .into(binding.imgMovie)

            binding.watchlist.setImageResource(
                if (movie.watchlist) R.drawable.ic_favorite else R.drawable.ic_not_favorite
            )

            binding.root.setOnClickListener {
                onMovieClick(movie)
            }

            binding.watchlist.setOnClickListener {
                onWishListMovieClick(movie, adapterPosition)
            }
        }
    }

    /**
     * Flattens the grouped movies into a single list of alternating headers and items.
     */
    private fun flattenGroupedMovies() {
        flattenedList.clear()
        groupedMovies.forEach { (year, movies) ->
            flattenedList.add(year) // Add year as a header
            flattenedList.addAll(movies) // Add movies for that year
        }
    }

    /**
     * Updates the grouped movies and refreshes the adapter.
     */
    fun updateGroupedMovies(newGroupedMovies: GroupedMovies) {
        groupedMovies = newGroupedMovies
        flattenGroupedMovies()
        notifyDataSetChanged()
    }
}
