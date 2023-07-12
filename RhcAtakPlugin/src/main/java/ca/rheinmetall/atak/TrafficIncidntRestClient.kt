package ca.rheinmetall.atak

import android.util.Log
import ca.rheinmetall.atak.json.route.TrafficIncidentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrafficIncidntRestClient {

    companion object {
        val TAG = "TrafficIncidntRestClien"
        val instance = TrafficIncidntRestClient()
    }

    private var api: TrafficIncidentApi? = null

    fun getTrafficIncidentList(retrofitEventListener: RetrofitEventListener) {
        val retrofit = NetworkClient2.retrofitClient
        api = retrofit.create(TrafficIncidentApi::class.java)

        val apiCall = api!!.getTrafficIncidentList(45.395557,-73.373854,45.622682,-74.159889, key, TrafficIncidentType.Construction.typeCode, Severity.Serious.severityCode,"json")

        Log.d(TAG, "request: ${apiCall.request()}")

        apiCall.enqueue(object : Callback<TrafficIncidentResponse> {

            override fun onResponse(call: Call<TrafficIncidentResponse>?, response: Response<TrafficIncidentResponse>?) {
                Log.d(TAG, response.toString())
                if (response?.body() != null) {
                    retrofitEventListener.onSuccess(call, response?.body())
                }
            }
            override fun onFailure(call: Call<TrafficIncidentResponse>?, t: Throwable?) {
                retrofitEventListener.onError(call, t)
            }
        })
    }
}