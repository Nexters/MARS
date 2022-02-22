package com.ojicoin.cookiepang.event

import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.domain.NotificationType.Ask
import com.ojicoin.cookiepang.domain.NotificationType.Transaction
import com.ojicoin.cookiepang.domain.ViewCount
import com.ojicoin.cookiepang.repository.NotificationRepository
import com.ojicoin.cookiepang.repository.ViewCountRepository
import com.ojicoin.cookiepang.util.NotificationMessageUtils
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EventHandler(
    private val viewCountRepository: ViewCountRepository,
    private val notificationRepository: NotificationRepository,
    private val notificationMessageUtils: NotificationMessageUtils,
) {
    @EventListener
    fun handleViewCookieEvent(viewCookieEvent: ViewCookieEvent) {
        viewCountRepository.save(
            ViewCount(
                userId = viewCookieEvent.userId,
                cookieId = viewCookieEvent.cookieId,
                count = 1,
            )
        )
    }

    @EventListener
    fun handlerNotificationEvent(notificationEvent: NotificationEvent) {
        val notification = when (notificationEvent) {
            is AskNotificationEvent -> Notification(
                type = Ask,
                title = Ask.title,
                receiverUserId = notificationEvent.receiverUserId,
                senderUserId = notificationEvent.senderUserId,
                createdAt = notificationEvent.createdAt,
                askId = notificationEvent.askId,

                content = notificationMessageUtils.getAskMessage(notificationEvent.cookieTitle),
            )

            is TransactionNotificationEvent -> Notification(
                type = Transaction,
                title = Transaction.title,
                receiverUserId = notificationEvent.receiverUserId,
                senderUserId = notificationEvent.senderUserId,
                cookieId = notificationEvent.cookieId,
                createdAt = notificationEvent.createdAt,

                content = notificationMessageUtils.getTransactionMessage(
                    notificationEvent.senderNickname,
                    notificationEvent.cookieTitle,
                    notificationEvent.hammerCount
                ),
            )
        }

        notificationRepository.save(notification)
    }
}
