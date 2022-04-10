package com.ojicoin.cookiepang.service

interface PushMessageOperations {
    fun send(destination: String, message: PushMessageContent): PushMessageResponse
}

class PushMessageResponse(response: String) {
    val messageId: String

    init {
        messageId = response.substringAfter("messages/")
    }
}

data class PushMessageContent(val title: String, val body: String, val image: String?)
