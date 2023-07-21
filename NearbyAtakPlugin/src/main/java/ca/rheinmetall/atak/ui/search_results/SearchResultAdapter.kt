package ca.rheinmetall.atak.ui.search_results

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ca.rheinmetall.atak.DistanceFormatter
import ca.rheinmetall.atak.databinding.SearchResultViewHolderBinding
import java.util.function.Consumer

class SearchResultAdapter constructor(
    private val pluginContext: Context,
    private val distanceFormatter: DistanceFormatter,
    private val onClick: Consumer<SelectableSearchResult>
) : ListAdapter<SelectableSearchResult, SearchResultViewHolder>(SearchResultDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = SearchResultViewHolderBinding.inflate(LayoutInflater.from(pluginContext), parent, false)
        return SearchResultViewHolder(binding, distanceFormatter, onClick)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SearchResultDiffUtil : DiffUtil.ItemCallback<SelectableSearchResult>() {
        override fun areItemsTheSame(oldItem: SelectableSearchResult, newItem: SelectableSearchResult) = oldItem.result.entityID == newItem.result.entityID

        override fun areContentsTheSame(oldItem: SelectableSearchResult, newItem: SelectableSearchResult) = oldItem == newItem
    }
}