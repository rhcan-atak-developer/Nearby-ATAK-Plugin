package ca.rheinmetall.atak

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Observer
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import ca.rheinmetall.atak.dagger.MainExecutor
import ca.rheinmetall.atak.json.route.TrafficIncidentResponse
import ca.rheinmetall.atak.json.route.TrafficIncidentResult
import ca.rheinmetall.atak.lifecycle.PluginLifeCycled
import ca.rheinmetall.atak.map.MapViewPort
import ca.rheinmetall.atak.map.MapViewPortDetector
import ca.rheinmetall.atak.model.route.TrafficIncident
import ca.rheinmetall.atak.model.route.TrafficIncidentRepository
import ca.rheinmetall.atak.preference.PreferenceEnum
import ca.rheinmetall.atak.preference.PreferencesExtensions.getStringPreference
import ca.rheinmetall.atak.ui.incidents.IncidentsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrafficIncidentRestClient @Inject constructor(
    @MainExecutor private val executor: ScheduledExecutorService,
    @DefaultSharedPreferences private val sharedPreferences: SharedPreferences,
    private val mapViewPortDetector: MapViewPortDetector,
    private val trafficIncidentRepository: TrafficIncidentRepository) : PluginLifeCycled(),
    Observer<MapViewPort>, SharedPreferences.OnSharedPreferenceChangeListener {

    private var future: ScheduledFuture<*>? = null

    companion object {
        val TAG = "TrafficIncidentRestClient"
    }

    override fun start()
    {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        future =
            executor.scheduleAtFixedRate({ getTrafficIncidentList() }, 0, 1, TimeUnit.MINUTES)
        mapViewPortDetector._mapViewPortMutableLiveData.observeForever(this)
    }

    override fun stop()
    {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        mapViewPortDetector._mapViewPortMutableLiveData.removeObserver(this)
        future?.cancel(true)
        future = null
    }

    private var api: TrafficIncidentApi? = null

    private fun getTrafficIncidentList() {

        if(!sharedPreferences.getBoolean(IncidentsViewModel.INCIDENT_ENABLED_PREF_KEY, false))
            return

        val retrofit = NetworkClient2.retrofitClient
        api = retrofit.create(TrafficIncidentApi::class.java)

        val viewPort = mapViewPortDetector._mapViewPortMutableLiveData.value
        val apiKey = sharedPreferences.getStringPreference(PreferenceEnum.API_KEY)
        val severity = sharedPreferences.getInt(IncidentsViewModel.SEVERITY_PREF_KEY, Severity.Serious.severityCode)
        val type = sharedPreferences.getInt(IncidentsViewModel.TRAFFIC_INCIDENT_TYPE_PREF_KEY, TrafficIncidentType.Accident.typeCode)
        val apiCall = api!!.getTrafficIncidentList(viewPort!!.downRight.lat, viewPort.downRight.lon, viewPort.upperLeft.lat,viewPort.upperLeft.lon, apiKey,
            if (type == TrafficIncidentType.All.typeCode) null else type,
            if (severity == Severity.All.severityCode) null else severity,"json")

        Log.d(TAG, "request: ${apiCall.request()}")

        apiCall.enqueue(object : Callback<TrafficIncidentResponse> {

            override fun onResponse(
                call: Call<TrafficIncidentResponse>,
                response: Response<TrafficIncidentResponse>
            ) {
                trafficIncidentRepository.clearTrafficIncidents()
                response.body()?.trafficIncidentResponseData?.forEach{it.resources.forEach { Log.d(TAG, it.description?:"") }}
                response.body()?.trafficIncidentResponseData?.forEach{ it ->
                    run {
                        it.resources.forEach {
                            addTrafficIncident(it) }
                    }
                }
            }
            override fun onFailure(call: Call<TrafficIncidentResponse>, t: Throwable) {
                Log.e("TrafficIncidentRestClient", "onError: $call", t )
            }
        })
    }

    private fun addTrafficIncident(trafficIncidentResult: TrafficIncidentResult) {
        trafficIncidentResult.point?.coordinates?.let {
            trafficIncidentRepository.addTrafficIncident(TrafficIncident(it[0], it[1], trafficIncidentResult.title, trafficIncidentResult.description, trafficIncidentResult.roadClosed, trafficIncidentResult.incidentId, TrafficIncidentType.fromCode(trafficIncidentResult.type!!)))
        }
    }

    override fun onChanged(p0: MapViewPort?) {
        getTrafficIncidentList()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(IncidentsViewModel.SEVERITY_PREF_KEY == key || IncidentsViewModel.TRAFFIC_INCIDENT_TYPE_PREF_KEY == key || IncidentsViewModel.INCIDENT_ENABLED_PREF_KEY == key)
            getTrafficIncidentList()
    }
}