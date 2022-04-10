package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.domain.NotificationType
import com.ojicoin.cookiepang.repository.NotificationRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant

internal class NotificationServiceTest(
    @Autowired val sut: NotificationService,
    @Autowired val notificationRepository: NotificationRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun get() {
        val receiverId = fixture.giveMeOne(Long::class.java)
        val oldNotification = fixture.giveMeBuilder(Notification::class.java)
            .setNull("id")
            .set("type", NotificationType.Ask)
            .set("receiverUserId", receiverId)
            .set("createdAt", Instant.now())
            .sample()
        val mostRecentNotification = fixture.giveMeBuilder(Notification::class.java)
            .setNull("id")
            .set("type", NotificationType.Transaction)
            .set("receiverUserId", receiverId)
            .set("createdAt", Instant.now())
            .sample()

        notificationRepository.save(oldNotification)
        notificationRepository.save(mostRecentNotification)

        val notificationList = sut.get(receiverId, 0, 1)
        val foundNotification = notificationList[0]

        then(mostRecentNotification.title).isEqualTo(foundNotification.title)
        then(mostRecentNotification.type).isEqualTo(foundNotification.type)
        then(mostRecentNotification.receiverUserId).isEqualTo(foundNotification.receiverUserId)
        then(mostRecentNotification.senderUserId).isEqualTo(foundNotification.senderUserId)
    }

}
