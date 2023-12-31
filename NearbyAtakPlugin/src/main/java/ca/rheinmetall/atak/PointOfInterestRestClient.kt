package ca.rheinmetall.atak

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import ca.rheinmetall.atak.dagger.DefaultSharedPreferences
import ca.rheinmetall.atak.dagger.MainExecutor
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.json.PointOfInterestResponse
import ca.rheinmetall.atak.map.MapViewPort
import ca.rheinmetall.atak.model.PointOfInterestType
import ca.rheinmetall.atak.preference.PreferenceEnum
import ca.rheinmetall.atak.preference.PreferencesExtensions.getStringPreference
import com.atakmap.android.maps.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Inject
import javax.inject.Singleton

private const val numberOfResults = 100

@Singleton
class PointOfInterestRestClient @Inject constructor(
    private val mapView: MapView,
    @MainExecutor private val executor: ScheduledExecutorService,
    @PluginContext private val pluginContext: Context,
    @DefaultSharedPreferences private val sharedPreferences: SharedPreferences,
) {
    lateinit var viewPort: MapViewPort
    private var api: PointOfInterestApi? = null

    fun getPointOfInterestsInCurrentMapView(categories: List<PointOfInterestType>, retrofitEventListener: RetrofitEventListener) {
        getPointOfInterests(categories, createSpatialFilterWithBBox(), retrofitEventListener)
    }

    fun getPointOfInterestAroundSelf(categories: List<PointOfInterestType>, radius: Int, retrofitEventListener: RetrofitEventListener) {
        val spatialFilter = createSpatialFilter(mapView.selfMarker.point.latitude, mapView.selfMarker.point.longitude, radius.toDouble())
        getPointOfInterests(categories, spatialFilter, retrofitEventListener)
    }

    fun getPointOfInterests(categories: List<PointOfInterestType>, spatialFilter: String, retrofitEventListener: RetrofitEventListener) {
        val retrofit = NetworkClient.retrofitClient
        api = retrofit.create(PointOfInterestApi::class.java)

        val filter = createFilter(categories.flatMap { it.ids })
        val apiKey = sharedPreferences.getStringPreference(PreferenceEnum.API_KEY)
        val apiCall = api!!.getPointOfInterestList(spatialFilter, filter, numberOfResults, createSelect(), apiKey, "json")

        Log.d("PointOfInterestRestClient", "request: ${apiCall.request()}")
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */

        apiCall.enqueue(object : Callback<PointOfInterestResponse> {

            override fun onResponse(call: Call<PointOfInterestResponse>?, response: Response<PointOfInterestResponse>?) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                Log.d("PointOfInterestRestClient", response.toString())
                if (response?.body() != null) {
                    retrofitEventListener.onSuccess(call, response.body())
                }
            }

            override fun onFailure(call: Call<PointOfInterestResponse>?, t: Throwable?) {
                /*
                Error callback
                */

                executor.execute {
                    Toast.makeText(
                        pluginContext,
                        "Unable to obtain results, make sure you are connected to Internet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                retrofitEventListener.onError(call, t)
            }
        })
    }

    private fun createSelect(): String {
        return "EntityID,DisplayName,Latitude,Longitude,__Distance"
    }

    private fun createFilter(types: List<Int>): String {
        return types.joinToString(separator = "%20Or%20") { "EntityTypeID%20eq%20\'${it}\'" }
    }

    private fun createSpatialFilterWithBBox(): String {
        return "bbox(${viewPort.downLeft.lat},${viewPort.downLeft.lon},${viewPort.upperRight.lat},${viewPort.upperRight.lon})"
    }

    private fun createSpatialFilter(lat: Double, lon: Double, radius: Double): String {
        return "nearby(${lat},${lon},${radius})"
    }
}
