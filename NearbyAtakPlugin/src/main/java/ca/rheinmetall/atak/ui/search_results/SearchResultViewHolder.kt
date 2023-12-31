package ca.rheinmetall.atak.ui.search_results

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ca.rheinmetall.atak.DistanceFormatter
import ca.rheinmetall.atak.databinding.SearchResultViewHolderBinding
import ca.rheinmetall.atak.model.PointOfInterestType
import com.atakmap.android.icons.UserIconDatabase
import com.atakmap.android.maps.MapView
import java.util.function.Consumer

class SearchResultViewHolder(
    private val binding: SearchResultViewHolderBinding,
    private val distanceFormatter: DistanceFormatter,
    private val onClick: Consumer<SelectableSearchResult>
) : ViewHolder(binding.root) {
    private val userIconDatabase = UserIconDatabase.instance(MapView._mapView.context)
    private val iconset = userIconDatabase.getIconSet("6d781afb-89a6-4c07-b2b9-a89748b6a38f", true, true)
    fun bind(result: SelectableSearchResult) {
        binding.container.setOnClickListener { onClick(result) }
        binding.displayName.text = result.result.displayName
        binding.address.text = result.result.address
        binding.distance.text = distanceFormatter.format(result.distance)
        val type = PointOfInterestType.values().firstOrNull { it.ids.contains(result.result.entityTypeID) }

        val icon = iconset.getIcon(type?.imageName)
        val bitmap = UserIconDatabase.instance(MapView._mapView.context).getIconBitmap(icon.id)
        binding.imageView.setImageBitmap(bitmap)

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
