package ca.rheinmetall.atak.mapgroup

import android.content.Context
import ca.rheinmetall.atak.application.PluginOwner
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.model.PointOfInterestRepository
import ca.rheinmetall.atak.model.PointOfInterestType
import com.atakmap.android.icons.UserIconDatabase
import com.atakmap.android.maps.DefaultMapGroup
import com.atakmap.android.maps.MapView
import com.atakmap.android.maps.Marker
import com.atakmap.coremap.filesystem.FileSystemUtils
import com.atakmap.coremap.maps.coords.GeoPoint
import java.util.EnumMap
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PointOfInterestMapGroup @Inject constructor(
    pointOfInterestRepository: PointOfInterestRepository,
    pluginOwner: PluginOwner,
    @PluginContext pluginContext: Context
) : DefaultMapGroup("Point Of Interest") {
    private val userIconDatabase = UserIconDatabase.instance(MapView._mapView.context)
    private val iconset = userIconDatabase.getIconSet("6d781afb-89a6-4c07-b2b9-a89748b6a38f", true, true)
    private val childrenGroup: EnumMap<PointOfInterestType, DefaultMapGroup> = EnumMap(PointOfInterestType::class.java)

    init {
        setMetaString("menu_factory_class", "PointOfInterest")
        pointOfInterestRepository.pointOfInterests.observe(pluginOwner) {
            it.forEach { poi -> addOrUpdate(poi) }
        }
        PointOfInterestType.values().forEach {
            val child = DefaultMapGroup(pluginContext.getString(it.stringRes))
            addGroup(child)
            childrenGroup[it] = child
        }
    }

    private fun addOrUpdate(poi: PointOfInterest) {
        val childGroup = childrenGroup[poi.pointOfInterestIcon]
        val oldMarker = childGroup?.deepFindUID(poi.uuid) as Marker?
        if (oldMarker == null) {
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

            childGroup?.addItem(marker)
        } else {
            val icon = iconset.getIcon(poi.pointOfInterestIcon.imageName)
            oldMarker.apply {
                point = poi.point
                type = icon.get2525cType()
                title = poi.name ?: poi.pointOfInterestIcon.name
                if (!FileSystemUtils.isEmpty(icon.iconsetPath))
                    setMetaString("IconsetPath", icon.iconsetPath)
            }

        }
    }
}