package ca.rheinmetall.atak.ui.incidents

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.rheinmetall.atak.Severity
import ca.rheinmetall.atak.TrafficIncidentType
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import javax.inject.Inject

class IncidentsViewModel @Inject constructor(
    @DefaultSharedPreferences private val sharedPreferences: SharedPreferences
)
 : ViewModel() {
    companion object {
        const val SEVERITY_PREF_KEY = "ca.rheinmetall.atak.SELECTED_SEVERITY"
        const val TRAFFIC_INCIDENT_TYPE_PREF_KEY = "ca.rheinmetall.atak.SELECTED_TRAFFIC_INCIDENT_TYPE"
        const val INCIDENT_ENABLED_PREF_KEY = "ca.rheinmetall.atak.INCIDENT_ENABLED_PREF_KEY"
    }

    private val _selectedSeverity = MutableLiveData<Severity>()
    val selectedSeverity: LiveData<Severity> = _selectedSeverity
    private val _selectedTrafficIncidentType = MutableLiveData<TrafficIncidentType>()
    val selectedTrafficIncidentType: LiveData<TrafficIncidentType> = _selectedTrafficIncidentType
    private val _enabled = MutableLiveData(false)
    val isEnabled: LiveData<Boolean> = _enabled

    init {
        _selectedSeverity.value = Severity.fromCode(
            sharedPreferences.getInt(
                SEVERITY_PREF_KEY,
                Severity.Serious.severityCode
            )
        )
        _selectedTrafficIncidentType.value = TrafficIncidentType.fromCode(
            sharedPreferences.getInt(
                TRAFFIC_INCIDENT_TYPE_PREF_KEY,
                TrafficIncidentType.Accident.typeCode
            )
        )
        _enabled.value = sharedPreferences.getBoolean(INCIDENT_ENABLED_PREF_KEY, false)
    }

    fun setEnabled(isSelected : Boolean) {
        sharedPreferences.edit().putBoolean(INCIDENT_ENABLED_PREF_KEY, isSelected).apply()
    }

    fun selectSeverity(severity: Severity?) {
        _selectedSeverity.value = severity
        sharedPreferences.edit().putInt(SEVERITY_PREF_KEY, _selectedSeverity.value!!.severityCode).apply()
    }

    fun selectTrafficIncident(trafficIncidentType: TrafficIncidentType) {
        _selectedTrafficIncidentType.value = trafficIncidentType
        sharedPreferences.edit().putInt(TRAFFIC_INCIDENT_TYPE_PREF_KEY, _selectedTrafficIncidentType.value!!.typeCode).apply()
    }
}