package ca.rheinmetall.atak.json

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchResultsRepository @Inject constructor() {
    private val _results = MutableLiveData<List<PointOfInterestResult>>()
    val results: LiveData<List<PointOfInterestResult>> = _results

    fun setResults(newResults: List<PointOfInterestResult>) {
        _results.postValue(newResults)
    }
}