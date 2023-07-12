package ca.rheinmetall.atak.model.route

import androidx.lifecycle.MutableLiveData
import ca.rheinmetall.atak.lifecycle.PluginLifeCycled
import ca.rheinmetall.atak.model.PointOfInterest
import com.atakmap.android.maps.MapEvent
import com.atakmap.android.maps.MapEventDispatcher
import com.atakmap.android.maps.MapView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrafficIncidentRepository @Inject constructor(private val mapView: MapView) : PluginLifeCycled(),
    MapEventDispatcher.MapEventDispatchListener {
    private val _trafficIncidents: MutableMap<String?, TrafficIncident> = HashMap()
    fun addTrafficIncident(trafficIncident: TrafficIncident) {
         _trafficIncidents[trafficIncident.uuid] = trafficIncident
        trafficIncidents.postValue(_trafficIncidents.values.toList())
    }

    override fun start() {
        mapView.mapEventDispatcher.addMapEventListener(this)
    }

    override fun stop() {
        mapView.mapEventDispatcher.removeMapEventListener(this)
    }
    
    fun clearTrafficIncidents() {
        _trafficIncidents.clear()
    }

    val trafficIncidents: MutableLiveData<List<TrafficIncident>> = MutableLiveData(ArrayList())

    override fun onMapEvent(mapEvent: MapEvent?) {
        if (mapEvent?.type == MapEvent.ITEM_REMOVED) {
            _trafficIncidents.remove(mapEvent.item.uid)
        }
    }
}