package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.repository.NotificationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun get(receiverUserId: Long, page: Int, size: Int): List<Notification> {
        val pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending())

        return notificationRepository.findAllByReceiverUserId(receiverUserId, pageRequest)
    }
}
