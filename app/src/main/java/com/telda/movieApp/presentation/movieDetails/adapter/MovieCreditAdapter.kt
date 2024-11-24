package com.telda.movieApp.presentation.movieDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.telda.movieApp.data.model.DepartmentTypes
import com.telda.movieApp.databinding.ItemHeadlineBinding
import com.telda.movieApp.databinding.ItemMovieCreditBinding
import com.telda.movieApp.domain.model.MoviePerson

class MovieCreditAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Data for grouped crew members by department
    private var crewGroups: Map<DepartmentTypes, List<MoviePerson>> = emptyMap()

    // List of departments sorted alphabetically for consistent ordering
    private var departments: List<DepartmentTypes> = emptyList()

    companion object {
        private const val VIEW_TYPE_HEADER = 0 // Header view type for department name
        private const val VIEW_TYPE_ITEM = 1   // Item view type for crew member
    }

    /**
     * ViewHolder for department headers.
     */
    inner class HeaderViewHolder(
        private val binding: ItemHeadlineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(department: DepartmentTypes) {
            binding.headline.text = department.name
                .lowercase()
                .replaceFirstChar { it.uppercase() } // Capitalize first character
        }
    }

    /**
     * ViewHolder for individual crew members.
     */
    inner class CrewViewHolder(
        private val binding: ItemMovieCreditBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(person: MoviePerson) {
            binding.apply {
                title.text = person.name

                Glide.with(imgMovie.context)
                    .load("https://image.tmdb.org/t/p/w185${person.profilePath}")
                    .circleCrop() // Circular crop for profile images
                    .into(imgMovie)
            }
        }
    }

    /**
     * Determines the type of ViewHolder to create based on view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemHeadlineBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_ITEM -> CrewViewHolder(
                ItemMovieCreditBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    /**
     * Binds data to the appropriate ViewHolder based on position.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (department, personIndex) = getItemInfo(position)

        when (holder) {
            is HeaderViewHolder -> holder.bind(department) // Bind department name
            is CrewViewHolder -> {
                crewGroups[department]?.get(personIndex)?.let { holder.bind(it) }
            }
        }
    }

    /**
     * Returns the total item count (headers + all items for each department).
     */
    override fun getItemCount(): Int {
        return departments.sumOf { dept ->
            1 + (crewGroups[dept]?.size ?: 0) // Include header + items for each department
        }
    }

    /**
     * Determines the view type based on position.
     */
    override fun getItemViewType(position: Int): Int {
        val (_, personIndex) = getItemInfo(position)
        return if (personIndex == -1) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    /**
     * Maps a position to its corresponding department and crew member index.
     *
     * @param position The position in the adapter.
     * @return A pair of department and person index (-1 for header).
     */
    private fun getItemInfo(position: Int): Pair<DepartmentTypes, Int> {
        var currentPosition = 0

        departments.forEach { department ->
            // Check if the position corresponds to a header
            if (currentPosition == position) {
                return department to -1
            }

            val itemCount = crewGroups[department]?.size ?: 0
            // Check if the position corresponds to an item within the department
            if (position <= currentPosition + itemCount) {
                return department to (position - currentPosition - 1)
            }

            // Move to the next department: include header + all items
            currentPosition += 1 + itemCount
        }

        throw IndexOutOfBoundsException("Invalid position: $position")
    }

    /**
     * Submits new data to the adapter and updates the UI.
     *
     * @param data A map of departments to their respective crew members.
     */
    fun submitList(data: Map<DepartmentTypes, List<MoviePerson>>) {
        crewGroups = data
        departments = data.keys.sortedBy { it.name } // Sort departments alphabetically
        notifyDataSetChanged()
    }
}
