package com.rohanNarayan.omnicrosswords.ui.upload

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UploadApi {
    @POST("parsePuzfile")
    suspend fun uploadFile(
        @Header("Authorization") bearer: String,
        @Body body: RequestBody
    ): Response<ResponseBody>
}