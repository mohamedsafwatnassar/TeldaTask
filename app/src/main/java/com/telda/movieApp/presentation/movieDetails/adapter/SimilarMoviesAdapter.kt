package com.telda.movieApp.presentation.movieDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.telda.movieApp.R
import com.telda.movieApp.databinding.ItemSimilarMovieBinding
import com.telda.movieApp.domain.model.Movie
import com.telda.movieApp.util.Constant

class SimilarMoviesAdapter(
    private val onSimilarMovieClick: (movieId: Int) -> Unit,
) : RecyclerView.Adapter<SimilarMoviesAdapter.SimilarMovieViewHolder>() {

    private var similarMovies: List<Movie> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarMovieViewHolder {
        val binding =
            ItemSimilarMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimilarMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SimilarMovieViewHolder, position: Int) {
        holder.bind(similarMovies[position])
    }

    override fun getItemCount(): Int = similarMovies.size

    /**
     * ViewHolder for similar movie items.
     */
    inner class SimilarMovieViewHolder(private val binding: ItemSimilarMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.title.text = movie.title
            binding.overview.text = movie.overview

            val moviePosterUrl = Constant.BASE_IMAGE_URL + movie.posterPath

            Glide.with(binding.root.context)
                .load(moviePosterUrl)
                .into(binding.imgMovie)

            if (adapterPosition == 0) onSimilarMovieClick.invoke(movie.id)

            binding.root.setOnClickListener {
                onSimilarMovieClick(movie.id)
            }
        }
    }

    /**
     * Updates the similar movies and refreshes the adapter.
     */
    fun updateSimilarMovies(newSimilarMovies: List<Movie>) {
        similarMovies = newSimilarMovies
        notifyDataSetChanged()
    }
}
