package ca.rheinmetall.atak

import android.util.Log
import ca.rheinmetall.atak.json.PointOfInterestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val numberOfResults = 25

const val key = "AqVdzQoQCWuvE8AaBd19rFYuTD7-o-IVJb3EVkn-fX8jDV341L0GCcCln1OgfFWu"

class PointOfInterestRestClient {
    companion object {
        val instance = PointOfInterestRestClient()
    }

    private var api: PointOfInterestApi? = null

    fun getUserList(retrofitEventListener: RetrofitEventListener) {
        val retrofit = NetworkClient.retrofitClient
        api = retrofit.create(PointOfInterestApi::class.java)

        val spatialFilter = createSpatialFilter(38.88494, -76.99596, 5.0)
        val filter = createFilter(6000)
        val apiCall = api!!.getPointOfInterestList(spatialFilter, filter, numberOfResults, createSelect(), key, "json")

        Log.d("pbolduc", "request: ${apiCall.request()}")
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */

        apiCall.enqueue(object : Callback<PointOfInterestResponse> {

            override fun onResponse(call: Call<PointOfInterestResponse>?, response: Response<PointOfInterestResponse>?) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                Log.d("pbolduc", response.toString())
                if (response?.body() != null) {
                    retrofitEventListener.onSuccess(call, response?.body())
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

    private fun createFilter(type: Int): String {
        return "EntityTypeID%20eq%20\'${type}\'"
    }

    private fun createSpatialFilter(lat: Double, lon: Double, radius: Double): String {
        return "nearby(${lat},${lon},${radius})"
    }
}