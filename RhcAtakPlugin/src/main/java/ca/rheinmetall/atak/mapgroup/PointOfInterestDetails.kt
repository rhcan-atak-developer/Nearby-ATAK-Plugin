package ca.rheinmetall.atak.mapgroup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.databinding.PoiExtraDetailsBinding
import ca.rheinmetall.atak.json.SearchResultsRepository
import com.atakmap.android.cotdetails.extras.ExtraDetailsProvider
import com.atakmap.android.maps.MapItem
import javax.inject.Inject

const val POI_KEY = "ca.rheinmetall.poi.key"

class PointOfInterestDetails @Inject constructor(@PluginContext pluginContext: Context, private val repository: SearchResultsRepository) : ExtraDetailsProvider {
    private val binding = PoiExtraDetailsBinding.inflate(LayoutInflater.from(pluginContext), null, false)

    override fun getExtraView(item: MapItem?, p1: View?): View? {
        return item?.takeIf { it.getMetaBoolean(POI_KEY, false) }?.getView()
    }

    private fun MapItem.getView(): View? {
        val poi = repository.results.value?.firstOrNull { it.entityID == uid }
        poi ?: return null
        binding.addressValue.text = poi.address
        binding.countryRegionValue.text = poi.countryRegion
        binding.localityValue.text = poi.locality
        binding.nameValue.text = poi.name
        binding.phoneValue.text = poi.phone
        binding.postalCodeValue.text = poi.postalCode
        return binding.root
    }
}