package ca.rheinmetall.atak.model.route

import ca.rheinmetall.atak.TrafficIncidentType
import com.atakmap.coremap.maps.coords.GeoPoint
import java.util.*

data class TrafficIncident(val lat: Double, val lon: Double, val description: String?, val longDescription: String?, val roadIsClosed: Boolean?, val uuid: String? = UUID.randomUUID().toString(), val type:TrafficIncidentType) {
    val point = GeoPoint(lat, lon, 0.0)
}