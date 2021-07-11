package com.ts.alex.ts_chat.data.network.api

import com.ts.alex.ts_chat.BuildConfig
import com.ts.alex.ts_chat.CONTENT_TYPE
import com.ts.alex.ts_chat.SERVER_KEY_CONT
import com.ts.alex.ts_chat.domain.models.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=${BuildConfig.SERVER_KEY}$SERVER_KEY_CONT", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}