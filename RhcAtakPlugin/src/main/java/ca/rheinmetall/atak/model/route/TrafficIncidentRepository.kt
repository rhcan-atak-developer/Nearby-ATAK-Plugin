package ca.rheinmetall.atak.model.route

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import ca.rheinmetall.atak.lifecycle.PluginLifeCycled
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.ui.incidents.IncidentsViewModel
import com.atakmap.android.maps.MapEvent
import com.atakmap.android.maps.MapEventDispatcher
import com.atakmap.android.maps.MapView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrafficIncidentRepository @Inject constructor(private val mapView: MapView, @DefaultSharedPreferences private val sharedPreferences: SharedPreferences) : PluginLifeCycled(),
    MapEventDispatcher.MapEventDispatchListener,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val _trafficIncidents: MutableMap<String?, TrafficIncident> = HashMap()
    fun addTrafficIncident(trafficIncident: TrafficIncident) {
         _trafficIncidents[trafficIncident.uuid] = trafficIncident
        trafficIncidents.postValue(_trafficIncidents.values.toList())
    }

    override fun start() {
        mapView.mapEventDispatcher.addMapEventListener(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun stop() {
        mapView.mapEventDispatcher.removeMapEventListener(this)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
    
    fun clearTrafficIncidents() {
        _trafficIncidents.clear()
        trafficIncidents.postValue(_trafficIncidents.values.toList())
    }

    val trafficIncidents: MutableLiveData<List<TrafficIncident>> = MutableLiveData(ArrayList())

    override fun onMapEvent(mapEvent: MapEvent?) {
        if (mapEvent?.type == MapEvent.ITEM_REMOVED) {
            _trafficIncidents.remove(mapEvent.item.uid)
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(IncidentsViewModel.INCIDENT_ENABLED_PREF_KEY == key)
            clearTrafficIncidents()
    }
}
