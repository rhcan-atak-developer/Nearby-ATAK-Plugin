package ca.rheinmetall.atak.mapgroup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.databinding.TrafficIncidentExtraDetailsBinding
import ca.rheinmetall.atak.json.SearchResultsRepository
import com.atakmap.android.cotdetails.extras.ExtraDetailsProvider
import com.atakmap.android.maps.MapItem
import javax.inject.Inject

const val TRAFFIC_INCIDENT_KEY = "ca.rheinmetall.poi.key"

class TrafficIncidentDetails @Inject constructor(@PluginContext pluginContext: Context, private val repository: SearchResultsRepository) : ExtraDetailsProvider {
    private val binding = TrafficIncidentExtraDetailsBinding.inflate(LayoutInflater.from(pluginContext), null, false)

    override fun getExtraView(item: MapItem?, p1: View?): View? {
        return item?.takeIf { it.getMetaBoolean(TRAFFIC_INCIDENT_KEY, false) }?.getView(item)
    }

    private fun MapItem.getView(item: MapItem): View? {
        binding.descriptionValue.text = item.getMetaString("longDescription", "")
        binding.roadIsClosedValue.text = item.getMetaString("roadIsClosed", "Unknown")
        return binding.root
    }
}