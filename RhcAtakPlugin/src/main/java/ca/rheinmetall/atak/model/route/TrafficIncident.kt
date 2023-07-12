package ca.rheinmetall.atak.model.route

import com.atakmap.coremap.maps.coords.GeoPoint
import java.util.*

data class TrafficIncident(val lat: Double, val lon: Double, val description: String?, val uuid: String? = UUID.randomUUID().toString()) {
    val point = GeoPoint(lat, lon, 0.0)
}