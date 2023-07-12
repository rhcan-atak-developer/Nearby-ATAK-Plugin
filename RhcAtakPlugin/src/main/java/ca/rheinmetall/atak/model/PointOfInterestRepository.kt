package ca.rheinmetall.atak.model

import androidx.lifecycle.MutableLiveData
import ca.rheinmetall.atak.lifecycle.PluginLifeCycled
import com.atakmap.android.maps.MapEvent
import com.atakmap.android.maps.MapEventDispatcher
import com.atakmap.android.maps.MapView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointOfInterestRepository @Inject constructor(private val mapView: MapView) : PluginLifeCycled(), MapEventDispatcher.MapEventDispatchListener {
    private val _pointOfInterests: MutableMap<String, PointOfInterest> = HashMap()

    override fun start() {
        mapView.mapEventDispatcher.addMapEventListener(this)
    }

    override fun stop() {
        mapView.mapEventDispatcher.removeMapEventListener(this)
    }

    fun addPointOfInterests(newPointOfInterests: List<PointOfInterest>) {
        newPointOfInterests.forEach { _pointOfInterests[it.uuid] = it }
        pointOfInterests.postValue(_pointOfInterests.values.toList())
    }

    val pointOfInterests: MutableLiveData<List<PointOfInterest>> = MutableLiveData(ArrayList())
    override fun onMapEvent(mapEvent: MapEvent?) {
        if (mapEvent?.type == MapEvent.ITEM_REMOVED) {
            _pointOfInterests.remove(mapEvent.item.uid)
        }
    }
}