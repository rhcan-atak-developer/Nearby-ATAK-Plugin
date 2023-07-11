package ca.rheinmetall.atak

import ca.rheinmetall.atak.json.PointOfInterestResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PointOfInterestApi {
    @GET("REST/v1/data/Microsoft/PointsOfInterest?")
    fun getPointOfInterestList(@Query("spatialFilter", encoded = true) spatialFilter: String,
    @Query("\$filter", encoded = true) filter: String,
    @Query("top", encoded = true) top: Int,
    @Query("select", encoded = true) select: String,
    @Query("key", encoded = true) key: String,
    @Query("\$format", encoded = true) format: String): Call<PointOfInterestResponse>
}