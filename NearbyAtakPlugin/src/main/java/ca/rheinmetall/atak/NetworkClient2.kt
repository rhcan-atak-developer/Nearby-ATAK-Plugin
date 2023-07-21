package ca.rheinmetall.atak

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient2 {
    private val BASE_URL = "http://dev.virtualearth.net/"
    private val TIMEOUT = 10
    var retrofit: Retrofit? = null

    val retrofitClient: Retrofit
        get() {
            if (retrofit == null) {
                val okHttpClientBuilder = OkHttpClient.Builder()
                okHttpClientBuilder.connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .client(okHttpClientBuilder.build())
                        .build()
            }
            return retrofit!!
        }
}