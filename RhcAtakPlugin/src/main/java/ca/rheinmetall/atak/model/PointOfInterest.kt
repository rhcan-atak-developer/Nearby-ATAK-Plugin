package ca.rheinmetall.atak.model

import com.atakmap.coremap.maps.coords.GeoPoint
import java.util.UUID

data class PointOfInterest(val lat: Double, val lon: Double, val pointOfInterestIcon: PointOfInterestType, val uuid: String = UUID.randomUUID().toString(),  val name: String? = null) {
    val point = GeoPoint(lat, lon, 0.0)
}