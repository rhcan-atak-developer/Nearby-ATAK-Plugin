package ca.rheinmetall.atak.mapgroup

import ca.rheinmetall.atak.application.PluginOwner
import ca.rheinmetall.atak.model.route.TrafficIncident
import ca.rheinmetall.atak.model.route.TrafficIncidentRepository
import com.atakmap.android.icons.UserIconDatabase
import com.atakmap.android.maps.*
import com.atakmap.coremap.filesystem.FileSystemUtils
import com.atakmap.coremap.maps.coords.GeoPoint
import javax.inject.Inject
import javax.inject.Singleton


private const val INCIDENT_ICON = "traffic_jam.png"

@Singleton
class TrafficIncidentMapGroup @Inject constructor(
    trafficIncidentRepository: TrafficIncidentRepository,
    pluginOwner: PluginOwner
) : DefaultMapGroup("Traffic Incidents"), MapGroup.OnItemListChangedListener {
    private val currents = HashMap<String?, TrafficIncident>()
    private val markers = HashMap<String?, Marker>()
    private val userIconDatabase = UserIconDatabase.instance(MapView._mapView.context)
    private val iconset = userIconDatabase.getIconSet("6d781afb-89a6-4c07-b2b9-a89748b6a38f", true, true)

    init {
        setMetaString("menu_factory_class", "TrafficIncident")
        addOnItemListChangedListener(this)
        trafficIncidentRepository.trafficIncidents.observe(pluginOwner) {
            it.forEach { incident -> addIncident(incident) }
        }
    }

    private fun addIncident(trafficIncident: TrafficIncident) {
        if (!currents.contains(trafficIncident.uuid)) {
            currents[trafficIncident.uuid] = trafficIncident
            val position = GeoPoint(trafficIncident.lat, trafficIncident.lon, 0.0)
            val icon = iconset.getIcon(INCIDENT_ICON)

            val marker = Marker(trafficIncident.uuid)
            marker.apply {
                type = icon.get2525cType()
                if (!FileSystemUtils.isEmpty(icon.iconsetPath))
                    setMetaString("IconsetPath", icon.iconsetPath)
                title = trafficIncident.description ?: "incident ${trafficIncident.uuid}"
                movable = false
                editable = false
                point = position
            }
            marker.setMetaBoolean("neverCot", true)

            addItem(marker)
            markers[trafficIncident.uuid] = marker
        } else {
            currents[trafficIncident.uuid] = trafficIncident
            val icon = iconset.getIcon(INCIDENT_ICON)
            markers[trafficIncident.uuid]?.apply {
                point = trafficIncident.point
                type = icon.get2525cType()
                title = trafficIncident.description ?: "incident ${trafficIncident.uuid}"
            }

        }
    }

    override fun onItemAdded(p0: MapItem?, p1: MapGroup?) {

    }

    override fun onItemRemoved(p0: MapItem, p1: MapGroup?) {
        currents.remove(p0.uid)
        markers.remove(p0.uid)
    }
}