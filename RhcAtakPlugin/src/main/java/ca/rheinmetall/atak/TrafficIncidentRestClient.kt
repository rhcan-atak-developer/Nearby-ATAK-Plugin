package ca.rheinmetall.atak

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Observer
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import ca.rheinmetall.atak.dagger.MainExecutor
import ca.rheinmetall.atak.json.route.TrafficIncidentResponse
import ca.rheinmetall.atak.json.route.TrafficIncidentResult
import ca.rheinmetall.atak.map.MapViewPort
import ca.rheinmetall.atak.map.MapViewPortDetector
import ca.rheinmetall.atak.model.route.TrafficIncident
import ca.rheinmetall.atak.model.route.TrafficIncidentRepository
import ca.rheinmetall.atak.ui.PointOfInterestViewModel
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
    private val trafficIncidentRepository: TrafficIncidentRepository) :
    Observer<MapViewPort>, SharedPreferences.OnSharedPreferenceChangeListener {

    private var future: ScheduledFuture<*>? = null
    private var eventListener : RetrofitEventListener? = null

    companion object {
        val TAG = "TrafficIncidentRestClient"
    }

    fun start()
    {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        future =
            executor.scheduleAtFixedRate({ getTrafficIncidentList() }, 0, 1, TimeUnit.MINUTES)
        mapViewPortDetector._mapViewPortMutableLiveData.observeForever(this)
    }

    fun stop()
    {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        mapViewPortDetector._mapViewPortMutableLiveData.removeObserver(this)
        future?.cancel(true)
        future = null
    }

    private var api: TrafficIncidentApi? = null

    private fun getTrafficIncidentList() {
        if(eventListener == null)
            return

        val retrofit = NetworkClient2.retrofitClient
        api = retrofit.create(TrafficIncidentApi::class.java)

        val viewPort = mapViewPortDetector._mapViewPortMutableLiveData.value

        val apiCall = api!!.getTrafficIncidentList(viewPort!!.downRight.lat, viewPort.downRight.lon, viewPort.upperLeft.lat,viewPort.upperLeft.lon, key,
            sharedPreferences.getInt(PointOfInterestViewModel.TRAFFIC_INCIDENT_TYPE_PREF_KEY, TrafficIncidentType.Accident.typeCode),
            sharedPreferences.getInt(PointOfInterestViewModel.SEVERITY_PREF_KEY, Severity.Serious.severityCode),"json")

        Log.d(TAG, "request: ${apiCall.request()}")

        apiCall.enqueue(object : Callback<TrafficIncidentResponse> {

            override fun onResponse(
                call: Call<TrafficIncidentResponse>,
                response: Response<TrafficIncidentResponse>
            ) {
                response.body()?.trafficIncidentResponseData?.forEach{ it -> it.resources.forEach { addTrafficIncident(it) }}
                if (response.body() != null) {
                    eventListener?.onSuccess(call, response.body())
                }
            }
            override fun onFailure(call: Call<TrafficIncidentResponse>, t: Throwable) {
                eventListener?.onError(call, t)
            }
        })
    }

    private fun addTrafficIncident(trafficIncidentResult: TrafficIncidentResult) {
        trafficIncidentResult.point?.coordinates?.let {
            trafficIncidentRepository.addTrafficIncident(TrafficIncident(it[0], it[1], trafficIncidentResult.title, trafficIncidentResult.incidentId))
        }
    }

    fun retrofitEventListener(retrofitEventListener: RetrofitEventListener) {
        eventListener = retrofitEventListener;
    }

    override fun onChanged(p0: MapViewPort?) {
        getTrafficIncidentList()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if(PointOfInterestViewModel.SEVERITY_PREF_KEY == key || PointOfInterestViewModel.TRAFFIC_INCIDENT_TYPE_PREF_KEY == key)
            getTrafficIncidentList()
    }
}