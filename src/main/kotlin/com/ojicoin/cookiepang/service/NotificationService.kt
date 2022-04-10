package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.dto.NotificationNewCount
import com.ojicoin.cookiepang.repository.NotificationRepository
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {

    fun get(receiverUserId: Long, page: Int, size: Int): List<Notification> {
        val foundUser = userRepository.findById(receiverUserId)
            .orElseThrow { throw NoSuchElementException("receiverUserId is not found. receiverUserId=$receiverUserId") }

        val pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val notifications = notificationRepository.findAllByReceiverUserId(receiverUserId, pageRequest)
        foundUser.lastNotificationCheckedAt = Instant.now()
        userRepository.save(foundUser)

        return notifications
    }

    fun getNewCount(receiverUserId: Long): NotificationNewCount {
        val foundUser = userRepository.findById(receiverUserId)
            .orElseThrow { throw NoSuchElementException("receiverUserId is not found. receiverUserId=$receiverUserId") }

        val newCount =
            notificationRepository.countAllByCreatedAtAfter(foundUser.lastNotificationCheckedAt!!)

        return NotificationNewCount(newCount)
    }
}
