package com.rohanNarayan.omnicrosswords.ui.upload

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.getValue

object UploadClient {
    private const val BASE_URL = "https://omni-crosswords-server-rtluzv2sqq-uc.a.run.app"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Time to establish connection
            .writeTimeout(30, TimeUnit.SECONDS)   // Time to upload the file
            .readTimeout(30, TimeUnit.SECONDS)    // Time to wait for server response
            .build()
    }

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Optional for JSON responses
            .build()
    }
}
