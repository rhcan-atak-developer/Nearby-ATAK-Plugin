package ca.rheinmetall.atak.mapgroup

import ca.rheinmetall.atak.application.PluginOwner
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.model.PointOfInterestRepository
import com.atakmap.android.icons.UserIconDatabase
import com.atakmap.android.maps.DefaultMapGroup
import com.atakmap.android.maps.MapGroup
import com.atakmap.android.maps.MapItem
import com.atakmap.android.maps.MapView
import com.atakmap.android.maps.Marker
import com.atakmap.coremap.filesystem.FileSystemUtils
import com.atakmap.coremap.maps.coords.GeoPoint
import java.util.Objects
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PointOfInterestMapGroup @Inject constructor(
    pointOfInterestRepository: PointOfInterestRepository,
    pluginOwner: PluginOwner
) : DefaultMapGroup("Point Of Interest"), MapGroup.OnItemListChangedListener {
    private val currents = HashMap<String, PointOfInterest>()
    private val markers = HashMap<String, Marker>()
    private val userIconDatabase = UserIconDatabase.instance(MapView._mapView.context)
    private val iconset = userIconDatabase.getIconSet("6d781afb-89a6-4c07-b2b9-a89748b6a38f", true, true)

    init {
        setMetaString("menu_factory_class", "PointOfInterest")
        addOnItemListChangedListener(this)
        pointOfInterestRepository.pointOfInterests.observe(pluginOwner) {
            it.forEach { poi -> addOrUpdate(poi) }
        }
    }

    private fun addOrUpdate(poi: PointOfInterest) {
        if (!currents.contains(poi.uuid)) {
            currents[poi.uuid] = poi
            val position = GeoPoint(poi.lat, poi.lon, 0.0)
            val icon = iconset.getIcon(poi.pointOfInterestIcon.imageName)

            val marker = Marker(poi.uuid)
            marker.apply {
                type = icon.get2525cType()
                if (!FileSystemUtils.isEmpty(icon.iconsetPath))
                    setMetaString("IconsetPath", icon.iconsetPath)
                title = poi.name ?: poi.pointOfInterestIcon.name
                movable = false
                editable = false
                point = position
            }

            addItem(marker)
            markers[poi.uuid] = marker
        } else {
            currents[poi.uuid] = poi
            val icon = iconset.getIcon(poi.pointOfInterestIcon.imageName)
            markers[poi.uuid]?.apply {
                point = poi.point
                type = icon.get2525cType()
                title = poi.name ?: poi.pointOfInterestIcon.name
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