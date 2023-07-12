package ca.rheinmetall.atak.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ca.rheinmetall.atak.PointOfInterestRestClient
import ca.rheinmetall.atak.RetrofitEventListener
import ca.rheinmetall.atak.json.PointOfInterestResponse
import ca.rheinmetall.atak.json.PointOfInterestResult
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.model.PointOfInterestRepository
import ca.rheinmetall.atak.model.PointOfInterestType
import retrofit2.Call
import javax.inject.Inject

class PointOfInterestViewModel @Inject constructor(private val repository: PointOfInterestRepository): ViewModel() {
    private val _selectedCategories = MutableLiveData<List<PointOfInterestType>>()
    val selectedCategories: LiveData<List<PointOfInterestType>> = _selectedCategories

    fun selectCategories(categoriesSelectionState: List<Pair<Boolean, PointOfInterestType>>) {
        _selectedCategories.value = categoriesSelectionState.filter { it.first }.map { it.second }
    }

    fun searchPointOfInterests() {
        _selectedCategories.value?.let {
            PointOfInterestRestClient.instance.getPointOfInterests(it, object : RetrofitEventListener {
                override fun onSuccess(call: Call<*>, response: Any) {
                    if (response is PointOfInterestResponse) {
                        repository.addPointOfInterests(response.pointOfInterestResponseData?.results?.mapNotNull { it.toPointOfInterestModel() } ?: emptyList())
                    }
                }

                override fun onError(call: Call<*>, t: Throwable) {
                    Log.e("PointOfInterestViewModel", "onError: $call", t)
                }
            })
        } ?: Log.d("PointOfInterestViewModel", "No selected categories, no request made to bing service")
    }
}

private fun PointOfInterestResult.toPointOfInterestModel() : PointOfInterest? {
    val type = PointOfInterestType.values().filter { it.ids.contains(this.entityTypeID) }.firstOrNull()
    return type?.let { PointOfInterest(this.latitude!!, this.longitude!!, it, this.entityID!!, this.displayName ) }
}
