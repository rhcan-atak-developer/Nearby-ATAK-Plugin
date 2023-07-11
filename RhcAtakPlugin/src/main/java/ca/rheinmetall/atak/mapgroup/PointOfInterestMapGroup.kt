package ca.rheinmetall.atak.mapgroup

import ca.rheinmetall.atak.application.PluginOwner
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.model.PointOfInterestRepository
import com.atakmap.android.icons.UserIconDatabase
import com.atakmap.android.maps.MapView
import com.atakmap.android.maps.Marker
import com.atakmap.android.user.PlacePointTool.MarkerCreator
import com.atakmap.coremap.maps.coords.GeoPoint
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PointOfInterestMapGroup @Inject constructor(
    pointOfInterestRepository: PointOfInterestRepository,
    pluginOwner: PluginOwner,
) {
    private val currents = HashMap<UUID, PointOfInterest>()
    private val markers = HashMap<UUID, Marker>()
    private val userIconDatabase = UserIconDatabase.instance(MapView._mapView.context)
    private val iconset =
        userIconDatabase.getIconSet("6d781afb-89a6-4c07-b2b9-a89748b6a38f", true, true)

    init {
        pointOfInterestRepository.pointOfInterests.observe(pluginOwner) {
            it.forEach { poi -> addOrUpdate(poi) }
        }
    }

    private fun addOrUpdate(poi: PointOfInterest) {
        if (!currents.contains(poi.uuid)) {
            currents[poi.uuid] = poi
            val point = GeoPoint(poi.lat, poi.lon, 0.0)
            val icon = iconset.getIcon(poi.pointOfInterestIcon.imageName)

            val type: String? = icon.get2525cType()
            val marker: MarkerCreator = MarkerCreator(point)
                .setUid(poi.uuid.toString())
                .setType(type)
                .setIconPath(icon.iconsetPath)
                .showCotDetails(false)
                .setCallsign(poi.pointOfInterestIcon.name)

            markers[poi.uuid] = marker.placePoint()

        }
    }
}