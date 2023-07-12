package ca.rheinmetall.atak.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.rheinmetall.atak.PointOfInterestRestClient
import ca.rheinmetall.atak.RetrofitEventListener
import ca.rheinmetall.atak.Severity
import ca.rheinmetall.atak.application.RhcPluginBroadcastEnum
import ca.rheinmetall.atak.TrafficIncidentType
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import ca.rheinmetall.atak.json.PointOfInterestResponse
import ca.rheinmetall.atak.json.PointOfInterestResult
import ca.rheinmetall.atak.json.SearchResultsRepository
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.model.PointOfInterestRepository
import ca.rheinmetall.atak.model.PointOfInterestType
import com.atakmap.android.ipc.AtakBroadcast
import retrofit2.Call
import javax.inject.Inject

private const val CATEGORIES_PREF_KEY = "ca.rheinmetall.atak.SELECTED_POI_CATEGORIES"
private const val SEVERITY_PREF_KEY = "ca.rheinmetall.atak.SELECTED_SEVERITY"
private const val TRAFFIC_INCIDENT_TYPE_PREF_KEY = "ca.rheinmetall.atak.SELECTED_TRAFFIC_INCIDENT_TYPE"

class PointOfInterestViewModel @Inject constructor(
    private val repository: PointOfInterestRepository,
    @DefaultSharedPreferences private val sharedPreferences: SharedPreferences,
    private val pointOfInterestRestClient: PointOfInterestRestClient,
    private val researchResultsRepository: SearchResultsRepository)
 : ViewModel() {
    private val _selectedCategories = MutableLiveData<List<PointOfInterestType>>()
    val selectedCategories: LiveData<List<PointOfInterestType>> = _selectedCategories

    private val _selectedSeverity = MutableLiveData<Severity>()
    val selectedSeverity: LiveData<Severity> = _selectedSeverity
    private val _selectedTrafficIncidentType = MutableLiveData<TrafficIncidentType>()
    val selectedTrafficIncidentType: LiveData<TrafficIncidentType> = _selectedTrafficIncidentType

    init {
        _selectedCategories.value = sharedPreferences.getStringSet(CATEGORIES_PREF_KEY, emptySet())!!.map { PointOfInterestType.valueOf(it) }
        _selectedSeverity.value = Severity.fromCode(sharedPreferences.getInt(SEVERITY_PREF_KEY, Severity.Serious.severityCode))
        _selectedTrafficIncidentType.value = TrafficIncidentType.fromCode(sharedPreferences.getInt(TRAFFIC_INCIDENT_TYPE_PREF_KEY, TrafficIncidentType.Accident.typeCode))
    }

    fun selectCategories(categoriesSelectionState: List<Pair<Boolean, PointOfInterestType>>) {
        _selectedCategories.value = categoriesSelectionState.filter { it.first }.map { it.second }
        sharedPreferences.edit().putStringSet(CATEGORIES_PREF_KEY, _selectedCategories.value?.map { it.name }?.toSet()).apply()
    }

    fun selectSeverity(severity: Severity) {
        _selectedSeverity.value = severity
        sharedPreferences.edit().putInt(SEVERITY_PREF_KEY, _selectedSeverity.value!!.severityCode)
    }

    fun selectTrafficIncident(trafficIncidentType: TrafficIncidentType) {
        _selectedTrafficIncidentType.value = trafficIncidentType
        sharedPreferences.edit().putInt(TRAFFIC_INCIDENT_TYPE_PREF_KEY, _selectedTrafficIncidentType.value!!.typeCode)
    }

    fun searchPointOfInterests() {
        _selectedCategories.value?.let {
            pointOfInterestRestClient.getPointOfInterests(it, object : RetrofitEventListener {
                override fun onSuccess(call: Call<*>, response: Any) {
                    if (response is PointOfInterestResponse) {
                        researchResultsRepository.setResults(response.pointOfInterestResponseData?.results ?: emptyList())
                        AtakBroadcast.getInstance().sendBroadcast(RhcPluginBroadcastEnum.SHOW_SEARCH_RESULTS.createIntent())
                    }
                }

                override fun onError(call: Call<*>, t: Throwable) {
                    Log.e("PointOfInterestViewModel", "onError: $call", t)
                }
            })
        } ?: Log.d("PointOfInterestViewModel", "No selected categories, no request made to bing service")
    }
}

private fun PointOfInterestResult.toPointOfInterestModel(): PointOfInterest? {
    val type = PointOfInterestType.values().firstOrNull { it.ids.contains(this.entityTypeID) }
    return type?.let { PointOfInterest(this.latitude!!, this.longitude!!, it, this.entityID!!, this.displayName) }
}
