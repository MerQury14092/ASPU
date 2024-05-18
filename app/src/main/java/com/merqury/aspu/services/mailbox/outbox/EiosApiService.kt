package com.merqury.aspu.services.mailbox.outbox

import com.merqury.aspu.services.file.models.FileModel
import com.merqury.aspu.ui.navfragments.profile.secretPreferences
import com.merqury.aspu.ui.printlog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface EiosApiService {
    @Multipart
    @POST("FileChunkSave")
    fun uploadFileWithData(
        @Header("Authorization") authHeader: String,
        @Part("messageID") messageID: RequestBody,
        @Part("isFirstChunk") isFirstChunk: RequestBody,
        @Part("isLastChunk") isLastChunk: RequestBody,
        @Part("chunkNumber") chunkNumber: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}

fun sendFileToEiosMessage(messageID: Long, fileModel: FileModel) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://plany.agpu.net/api/Mail/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(EiosApiService::class.java)


// Создайте RequestBody из данных
    val messageID = messageID.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
    val isFirstChunk = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())
    val isLastChunk = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())
    val chunkNumber = "1".toRequestBody("multipart/form-data".toMediaTypeOrNull())

// Преобразуйте файловые данные в MultipartBody.Part
    val filePart = MultipartBody.Part.createFormData(
        "file",
        fileModel.fileName,
        fileModel.content!!.toRequestBody("application/octet-stream".toMediaTypeOrNull())
    )

    val token = "Bearer ${secretPreferences.getString("authToken", null)}"

// Выполните запрос
    val call =
        service.uploadFileWithData(
            token,
            messageID,
            isFirstChunk,
            isLastChunk,
            chunkNumber,
            filePart
        )
    call.enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                printlog("${fileModel.fileName} успешно")
            } else {
                printlog("${fileModel.fileName} error on response: $response")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            printlog("${fileModel.fileName} error on failure: $t")
        }
    })
}