package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.NotificationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class NotificationController(private val notificationService: NotificationService) {
    @GetMapping("/users/{userId}/notifications")
    fun getNotifications(
        @PathVariable userId: Long,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "3") size: Int,
    ) = notificationService.get(receiverId = userId, page = page, size = size)

    @GetMapping("/users/{userId}/notifications/newCount")
    fun getNewNotificationCount(
        @PathVariable userId: Long
    ) = notificationService.getNewCount(receiverId = userId)
}
