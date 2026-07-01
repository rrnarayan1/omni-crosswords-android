package com.rohanNarayan.omnicrosswords.ui.upload

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UploadClient {
    private const val BASE_URL = "https://omni-crosswords-server-rtluzv2sqq-uc.a.run.app"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Optional for JSON responses
            .build()
    }
}