package ca.rheinmetall.atak.ui.search_results

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import ca.rheinmetall.atak.application.RhcPluginBroadcastEnum
import ca.rheinmetall.atak.json.PointOfInterestResult
import ca.rheinmetall.atak.json.SearchResultsRepository
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.model.PointOfInterestRepository
import ca.rheinmetall.atak.model.PointOfInterestType
import com.atakmap.android.ipc.AtakBroadcast
import com.atakmap.android.maps.MapView
import com.atakmap.coremap.maps.coords.GeoPoint
import javax.inject.Inject

class SearchResultViewModel @Inject constructor(
    private val repository: SearchResultsRepository,
    private val pointOfInterestRepository: PointOfInterestRepository
) : ViewModel(), Observer<List<PointOfInterestResult>> {
    private val _results = MutableLiveData<List<SelectableSearchResult>>(emptyList())
    val results: LiveData<List<SelectableSearchResult>> = _results

    init {
        repository.results.observeForever(this)
    }

    fun triggerUpdate() {
        _results.value = _results.value?.let { ArrayList(it) }
    }

    private fun computeDistance(pointOfInterestResult: PointOfInterestResult): Double {
        val selfPosition = MapView._mapView.selfMarker.point
        return if (selfPosition.isValid) {
            selfPosition.distanceTo(GeoPoint(pointOfInterestResult.latitude!!, pointOfInterestResult.longitude!!))
        } else {
            Double.NaN
        }
    }

    fun addAllResult() {
        pointOfInterestRepository.addPointOfInterests(_results.value?.mapNotNull { it.result.toPointOfInterestModel() } ?: emptyList())
        AtakBroadcast.getInstance().sendBroadcast(RhcPluginBroadcastEnum.CLOSE_SEARCH_RESULTS.createIntent())
    }

    fun addSelectedResult() {
        pointOfInterestRepository.addPointOfInterests(_results.value?.filter { it.selected }?.mapNotNull { it.result.toPointOfInterestModel() } ?: emptyList())
        AtakBroadcast.getInstance().sendBroadcast(RhcPluginBroadcastEnum.CLOSE_SEARCH_RESULTS.createIntent())
    }


    private fun PointOfInterestResult.toPointOfInterestModel(): PointOfInterest? {
        val type = PointOfInterestType.values().firstOrNull { it.ids.contains(this.entityTypeID) }
        return type?.let { PointOfInterest(this.latitude!!, this.longitude!!, it, this.entityID!!, this.displayName) }
    }

    fun onDestroyView() {
        repository.results.removeObserver(this)
    }

    override fun onChanged(newResults: List<PointOfInterestResult>?) {
        _results.value = newResults?.map { SelectableSearchResult(it, false, computeDistance(it)) } ?: emptyList()
    }
}