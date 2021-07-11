package com.ts.alex.ts_chat.domain.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)
