package com.ojicoin.cookiepang.event

import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.domain.NotificationType.Ask
import com.ojicoin.cookiepang.domain.NotificationType.Transaction
import com.ojicoin.cookiepang.domain.ViewCount
import com.ojicoin.cookiepang.repository.NotificationRepository
import com.ojicoin.cookiepang.repository.UserRepository
import com.ojicoin.cookiepang.repository.ViewCountRepository
import com.ojicoin.cookiepang.service.PushMessageContent
import com.ojicoin.cookiepang.service.PushMessageOperations
import com.ojicoin.cookiepang.util.NotificationMessageUtils
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EventHandler(
    private val viewCountRepository: ViewCountRepository,
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val pushMessageOperations: PushMessageOperations,
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
                receiverUserId = notificationEvent.receiverId,
                senderUserId = notificationEvent.senderId,
                createdAt = notificationEvent.createdAt,
                askId = notificationEvent.askId,

                content = NotificationMessageUtils.getAskMessage(notificationEvent.cookieTitle),
            )

            is TransactionNotificationEvent -> {
                val senderNickname = userRepository.findById(notificationEvent.senderId).get().nickname

                Notification(
                    type = Transaction,
                    title = Transaction.title,
                    receiverUserId = notificationEvent.receiverId,
                    senderUserId = notificationEvent.senderId,
                    cookieId = notificationEvent.cookieId,
                    createdAt = notificationEvent.createdAt,

                    content = NotificationMessageUtils.getTransactionMessage(
                        senderNickname,
                        notificationEvent.cookieTitle,
                        notificationEvent.hammerCount
                    )
                )
            }
        }

        notificationRepository.save(notification)
        val userOptional = userRepository.findById(notification.receiverUserId)

        if (userOptional.isPresent) {
            val deviceToken = userOptional.get().deviceToken
            if (deviceToken.isNullOrBlank()) {
                pushMessageOperations.send(destination = deviceToken!!, PushMessageContent(title = notification.type.pushMessageTitle, body = notification.content, image = null))
            }
        }
    }
}
