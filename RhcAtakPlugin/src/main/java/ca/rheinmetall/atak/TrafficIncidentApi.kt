package ca.rheinmetall.atak

import ca.rheinmetall.atak.json.route.TrafficIncidentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrafficIncidentApi {
    @GET("REST/v1/Traffic/Incidents/{southLatitude},{westLongitude},{northLatitude},{eastLongitude}")
    fun getTrafficIncidentList(
        @Path("southLatitude") southLatitude: Double,
        @Path("westLongitude") westLongitude: Double,
        @Path("northLatitude") northLatitude: Double,
        @Path("eastLongitude") eastLongitude: Double,
        @Query("key", encoded = true) key: String,
        @Query("type", encoded = true) typeCode: Int,
        @Query("severity") severityCode: Int,
        @Query("\$format", encoded = true) format: String
    ): Call<TrafficIncidentResponse>
}