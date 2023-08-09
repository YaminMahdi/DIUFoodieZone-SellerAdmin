package com.diu.mlab.foodie.admin.data.data_source

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class NotificationMessage(val message: OrderNotifyInfo)
data class OrderNotifyInfo(val topic: String, val data: NotificationData)
data class NotificationData(val title: String, val body: String)

interface NotificationApi {
    companion object{
        const val BASE_URL="https://fcm.googleapis.com/"
    }

    @Headers("Content-Type: application/json")
    @POST("/v1/projects/diufoodizone/messages:send")
    suspend fun sendNotification(
        @Body message : NotificationMessage,
        @Header("Authorization") authHeader : String
    )
}