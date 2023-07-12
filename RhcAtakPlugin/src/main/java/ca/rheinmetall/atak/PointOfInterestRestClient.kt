package ca.rheinmetall.atak

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.contentValuesOf
import ca.rheinmetall.atak.dagger.MainExecutor
import ca.rheinmetall.atak.dagger.PluginContext
import ca.rheinmetall.atak.json.PointOfInterestResponse
import ca.rheinmetall.atak.model.PointOfInterestType
import ca.rheinmetall.atak.map.MapViewPort
import com.atakmap.android.maps.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Inject
import javax.inject.Singleton

private const val numberOfResults = 25

const val key = "AqVdzQoQCWuvE8AaBd19rFYuTD7-o-IVJb3EVkn-fX8jDV341L0GCcCln1OgfFWu"

@Singleton
class PointOfInterestRestClient @Inject constructor(private val mapView: MapView,
    @MainExecutor private val executor: ScheduledExecutorService,
    @PluginContext private val pluginContext: Context)
{
    lateinit var viewPort: MapViewPort
    private var api: PointOfInterestApi? = null

    fun getPointOfInterests(categories: List<PointOfInterestType>, retrofitEventListener: RetrofitEventListener) {
        val retrofit = NetworkClient.retrofitClient
        api = retrofit.create(PointOfInterestApi::class.java)

        val spatialFilter = createSpatialFilter(mapView.selfMarker.point.latitude, mapView.selfMarker.point.longitude, 5.0)
        val filter = createFilter(categories.flatMap { it.ids })
        val apiCall = api!!.getPointOfInterestList(spatialFilter, filter, numberOfResults, createSelect(), key, "json")

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

    fun createSpatialFilterWithIntersection(): String {
        return "intersects('POLYGON((${viewPort.upperLeft.lat},${viewPort.upperLeft.lon},${viewPort.downLeft.lat},${viewPort.downLeft.lon}," +
            "${viewPort.upperRight.lat},${viewPort.upperLeft.lon},${viewPort.downRight.lat},${viewPort.downRight.lon}))')"
    }

    private fun createSpatialFilter(lat: Double, lon: Double, radius: Double): String {
        return "nearby(${lat},${lon},${radius})"
    }
}
