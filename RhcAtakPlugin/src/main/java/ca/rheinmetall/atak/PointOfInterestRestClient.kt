package ca.rheinmetall.atak

import android.util.Log
import ca.rheinmetall.atak.json.PointOfInterestResponse
import ca.rheinmetall.atak.model.PointOfInterestType
import ca.rheinmetall.atak.map.MapViewPort
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val numberOfResults = 25

const val key = "AqVdzQoQCWuvE8AaBd19rFYuTD7-o-IVJb3EVkn-fX8jDV341L0GCcCln1OgfFWu"

class PointOfInterestRestClient {
    companion object {
        val instance = PointOfInterestRestClient()
    }

    lateinit var viewPort: MapViewPort
    private var api: PointOfInterestApi? = null

    fun getPointOfInterests(categories: List<PointOfInterestType>, retrofitEventListener: RetrofitEventListener) {
        val retrofit = NetworkClient.retrofitClient
        api = retrofit.create(PointOfInterestApi::class.java)

        val spatialFilter = createSpatialFilter(38.88494, -76.99596, 5.0)
//        val spatialFilter = createSpatialFilterWithIntersection()

        val filter = createFilter(categories.flatMap { it.ids })
        val apiCall = api!!.getPointOfInterestList(spatialFilter, filter, numberOfResults, createSelect(), key, "json")

        Log.d("pbolduc", "request: ${apiCall.request()}")
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */

        apiCall.enqueue(object : Callback<PointOfInterestResponse> {

            override fun onResponse(call: Call<PointOfInterestResponse>?, response: Response<PointOfInterestResponse>?) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                Log.d("KEK", response.toString())
                if (response?.body() != null) {
                    retrofitEventListener.onSuccess(call, response.body())
                }
            }
            override fun onFailure(call: Call<PointOfInterestResponse>?, t: Throwable?) {
                /*
                Error callback
                */
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

    }

    private fun createSpatialFilter(lat: Double, lon: Double, radius: Double): String {
        return "nearby(${lat},${lon},${radius})"
    }
}
