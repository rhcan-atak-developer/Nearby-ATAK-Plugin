package ca.rheinmetall.atak.model.route

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrafficIncidentRepository @Inject constructor(){
    private val _trafficIncidents: MutableMap<String?, TrafficIncident> = HashMap()
    fun addTrafficIncident(trafficIncident: TrafficIncident) {
         _trafficIncidents[trafficIncident.uuid] = trafficIncident
        trafficIncidents.postValue(_trafficIncidents.values.toList())
    }

    val trafficIncidents: MutableLiveData<List<TrafficIncident>> = MutableLiveData(ArrayList())
}