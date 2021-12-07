package com.cookandroid.lowest_price_alert

import com.google.gson.GsonBuilder
import com.squareup.okhttp.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    // query: "http:// url~/search?item=생수"
    // http://121.151.0.53:5000/search?item=생수
    @GET("search")
    fun getSearchItems(
        @Query("item") query: String
    ): Call<GetData>

    @GET("product")
    fun getProductNums(
        @Query("no") query: String
    ): Call<String>

    companion object {
        // base API url
        private const val BASE_URL_REST_API = "http://15.165.67.32:5000"

        fun create(): API {
            var gson = GsonBuilder().setLenient().create()

            val client = OkHttpClient.Builder()
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL_REST_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(API::class.java)
        }
    }
}