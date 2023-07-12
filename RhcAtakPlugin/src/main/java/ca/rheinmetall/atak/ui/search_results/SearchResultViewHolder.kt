package ca.rheinmetall.atak.ui.search_results

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ca.rheinmetall.atak.DistanceFormatter
import ca.rheinmetall.atak.databinding.SearchResultViewHolderBinding
import java.util.function.Consumer

class SearchResultViewHolder(
    private val binding: SearchResultViewHolderBinding,
    private val distanceFormatter: DistanceFormatter,
    private val onClick: Consumer<SelectableSearchResult>
) : ViewHolder(binding.root) {
    fun bind(result: SelectableSearchResult) {
        binding.container.setOnClickListener { onClick(result) }
        binding.displayName.text = result.result.displayName
        binding.address.text = result.result.address
        binding.distance.text = distanceFormatter.format(result.distance)

        updateSelectedState(result)
    }

    private fun onClick(result: SelectableSearchResult) {
        onClick.accept(result)
        updateSelectedState(result)
    }

    private fun updateSelectedState(result: SelectableSearchResult) {
        binding.container.isSelected = result.selected
        binding.displayName.isSelected = result.selected
        binding.address.isSelected = result.selected
        binding.distance.isSelected = result.selected
    }
}
