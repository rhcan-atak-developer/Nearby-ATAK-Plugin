package ca.rheinmetall.atak.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ca.rheinmetall.atak.PointOfInterestRestClient
import ca.rheinmetall.atak.RetrofitEventListener
import ca.rheinmetall.atak.application.NearbyPluginBroadcastEnum
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import ca.rheinmetall.atak.json.PointOfInterestResponse
import ca.rheinmetall.atak.json.PointOfInterestResult
import ca.rheinmetall.atak.json.SearchResultsRepository
import ca.rheinmetall.atak.model.PointOfInterest
import ca.rheinmetall.atak.model.PointOfInterestType
import com.atakmap.android.ipc.AtakBroadcast
import retrofit2.Call
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

private const val CATEGORIES_PREF_KEY = "ca.rheinmetall.atak.SELECTED_POI_CATEGORIES"
private const val SEARCH_OPTION_KEY = "ca.rheinmetall.atak.SEARCH_OPTION_KEY"
class PointOfInterestViewModel @Inject constructor(
    @DefaultSharedPreferences private val sharedPreferences: SharedPreferences,
    private val pointOfInterestRestClient: PointOfInterestRestClient,
    private val researchResultsRepository: SearchResultsRepository)
 : ViewModel() {
    private val radiusValue = intArrayOf(1, 2, 5, 10, 20, 50, 100)

    private val _selectedCategories = MutableLiveData<List<PointOfInterestType>>()
    val selectedCategories: LiveData<List<PointOfInterestType>> = _selectedCategories

    private val _selectedOption = MutableLiveData<SearchType>()
    val selectedOption: LiveData<SearchType> = _selectedOption

    private val _radius = MutableLiveData<Int>(0)
    val radius = Transformations.map(_radius) { radiusValue[it] }

    init {
        _selectedCategories.value = sharedPreferences.getStringSet(CATEGORIES_PREF_KEY, emptySet())!!.map { PointOfInterestType.valueOf(it) }
        _selectedOption.value = SearchType.valueOf(sharedPreferences.getString(SEARCH_OPTION_KEY, SearchType.VIEWPORT.name)!!)
    }

    fun selectCategories(categoriesSelectionState: List<Pair<Boolean, PointOfInterestType>>) {
        _selectedCategories.value = categoriesSelectionState.filter { it.first }.map { it.second }
        sharedPreferences.edit().putStringSet(CATEGORIES_PREF_KEY, _selectedCategories.value?.map { it.name }?.toSet()).apply()
    }

    fun searchPointOfInterests() {
        val retrofitEventListener = object : RetrofitEventListener {
            override fun onSuccess(call: Call<*>, response: Any) {
                if (response is PointOfInterestResponse) {
                    researchResultsRepository.setResults(response.pointOfInterestResponseData?.results ?: emptyList())
                    AtakBroadcast.getInstance().sendBroadcast(NearbyPluginBroadcastEnum.SHOW_SEARCH_RESULTS.createIntent())
                }
            }

            override fun onError(call: Call<*>, t: Throwable) {
                Log.e("PointOfInterestViewModel", "onError: $call", t)
            }
        }
        _selectedCategories.value?.let {
            if (_selectedOption.value == SearchType.VIEWPORT)
                pointOfInterestRestClient.getPointOfInterestsInCurrentMapView(it, retrofitEventListener)
            else
                pointOfInterestRestClient.getPointOfInterestAroundSelf(it, radius.value!!, retrofitEventListener)
        } ?: Log.d("PointOfInterestViewModel", "No selected categories, no request made to bing service")
    }

    fun selectSelfOption() {
        _selectedOption.value = SearchType.SELF
        saveSearchOption()
    }

    fun selectViewportOption() {
        _selectedOption.value = SearchType.VIEWPORT
        saveSearchOption()
    }

    private fun saveSearchOption() {
        sharedPreferences.edit().putString(SEARCH_OPTION_KEY, _selectedOption.value!!.name).apply()
    }

    fun increaseRadius() {
        _radius.value = min(_radius.value!! + 1, radiusValue.size - 1 )
    }

    fun reduceRadius() {
        _radius.value = max(_radius.value!! - 1, 0)
    }
}

private fun PointOfInterestResult.toPointOfInterestModel(): PointOfInterest? {
    val type = PointOfInterestType.values().firstOrNull { it.ids.contains(this.entityTypeID) }
    return type?.let { PointOfInterest(this.latitude!!, this.longitude!!, it, this.entityID!!, this.displayName) }
}
