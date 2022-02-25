package com.ojicoin.cookiepang.event

import org.springframework.context.ApplicationEvent
import java.time.Instant

data class ViewCookieEvent(
    private val source: Any,
    val userId: Long,
    val cookieId: Long,
) : ApplicationEvent(source) {
    override fun getSource(): Any {
        return super.getSource()
    }
}

sealed class NotificationEvent

data class AskNotificationEvent(
    val receiverUserId: Long,
    val senderUserId: Long,
    val askId: Long,
    val createdAt: Instant = Instant.now(),

    val cookieTitle: String,
) : NotificationEvent()

data class TransactionNotificationEvent(
    val receiverUserId: Long,
    val senderUserId: Long,
    val cookieId: Long,
    val createdAt: Instant = Instant.now(),

    val cookieTitle: String,
    val hammerCount: Long,
) : NotificationEvent()
