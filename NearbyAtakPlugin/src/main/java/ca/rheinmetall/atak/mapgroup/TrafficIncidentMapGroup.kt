package ca.rheinmetall.atak.mapgroup

import ca.rheinmetall.atak.application.PluginOwner
import ca.rheinmetall.atak.model.route.TrafficIncident
import ca.rheinmetall.atak.model.route.TrafficIncidentRepository
import com.atakmap.android.icons.UserIconDatabase
import com.atakmap.android.maps.DefaultMapGroup
import com.atakmap.android.maps.MapGroup
import com.atakmap.android.maps.MapItem
import com.atakmap.android.maps.MapView
import com.atakmap.android.maps.Marker
import com.atakmap.coremap.filesystem.FileSystemUtils
import com.atakmap.coremap.maps.coords.GeoPoint
import javax.inject.Inject
import javax.inject.Singleton


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
        trafficIncidentRepository.trafficIncidents.observe(pluginOwner) { trafficIncidents: List<TrafficIncident>? ->
            currents.filter { !(trafficIncidents?.contains(it.value) ?: false) }.forEach {
                removeItem(markers[it.key])
            }
            trafficIncidents?.forEach { incident -> addIncident(incident) }
        }
    }

    private fun addIncident(trafficIncident: TrafficIncident) {
        if (!currents.contains(trafficIncident.uuid)) {
            currents[trafficIncident.uuid] = trafficIncident
            val position = GeoPoint(trafficIncident.lat, trafficIncident.lon, 0.0)
            val icon = iconset.getIcon(trafficIncident.type.imageName)

            val marker = Marker(trafficIncident.uuid)
            marker.apply {
                type = icon.get2525cType() ?: "a-u-G"
                if (!FileSystemUtils.isEmpty(icon.iconsetPath))
                    setMetaString("IconsetPath", icon.iconsetPath)
                title = trafficIncident.description ?: "incident ${trafficIncident.uuid}"
                movable = false
                editable = false
                point = position
                setMetaBoolean(TRAFFIC_INCIDENT_KEY, true)
            }
            marker.setMetaBoolean("neverCot", true)
            marker.setMetaString("longDescription", trafficIncident.longDescription)
            trafficIncident.roadIsClosed?.let { marker.setMetaString("roadIsClosed", if (it) "Yes" else "No") }

            addItem(marker)
            markers[trafficIncident.uuid] = marker
        } else {
            currents[trafficIncident.uuid] = trafficIncident
            val icon = iconset.getIcon(trafficIncident.type.imageName)
            markers[trafficIncident.uuid]?.apply {
                point = trafficIncident.point
                type = icon.get2525cType() ?: "a-u-G"
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