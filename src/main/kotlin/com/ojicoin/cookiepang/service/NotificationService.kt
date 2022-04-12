package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.dto.NotificationNewCount
import com.ojicoin.cookiepang.repository.NotificationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
) {

    fun get(receiverUserId: Long, page: Int, size: Int): List<Notification> {
        val pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val notifications = notificationRepository.findAllByReceiverUserId(receiverUserId, pageRequest)

        val notificationsWithCheckedTrue = notifications.filter { notification -> !notification.checked }
            .map { notification ->
                notification.checked = true
                notification
            }
            .toList()

        notificationRepository.saveAll(notificationsWithCheckedTrue)

        return notifications
    }

    fun getNewCount(receiverUserId: Long): NotificationNewCount {
        val newCount =
            notificationRepository.countAllByReceiverUserIdAndChecked(receiverUserId, false)

        return NotificationNewCount(newCount)
    }
}
