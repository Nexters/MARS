package com.ojicoin.cookiepang.event

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.NotificationType
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.repository.NotificationRepository
import com.ojicoin.cookiepang.repository.UserRepository
import com.ojicoin.cookiepang.repository.ViewCountRepository
import com.ojicoin.cookiepang.util.NotificationMessageUtils
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher

class EventHandlerTest(
    @Autowired val eventPublisher: ApplicationEventPublisher,
    @Autowired val viewCountRepository: ViewCountRepository,
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val userRepository: UserRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun handleViewCookieEvent() {
        // given
        val viewCookieEvent = fixture.giveMeOne(ViewCookieEvent::class.java)

        // when
        eventPublisher.publishEvent(viewCookieEvent)

        // then
        then(viewCountRepository.findAll()).hasSize(1)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun handleNotificationEventForAsk() {
        // given
        val user = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .setNotNull("deviceToken")
            .sample()
        val savedReceiverUser = userRepository.save(user)

        val notificationEvent = fixture.giveMeBuilder(AskNotificationEvent::class.java)
            .set("receiverId", savedReceiverUser.id)
            .sample()

        // when
        eventPublisher.publishEvent(notificationEvent)

        val notifications = notificationRepository.findAll()

        // then
        then(notifications).hasSize(1)
        notifications.forEach {
            then(it.type).isEqualTo(NotificationType.Ask)
            then(it.title).isEqualTo("??????")
            then(it.receiverUserId).isEqualTo(notificationEvent.receiverId)
            then(it.senderUserId).isEqualTo(notificationEvent.senderId)
            then(it.createdAt).isEqualTo(notificationEvent.createdAt)
            then(it.askId).isEqualTo(notificationEvent.askId)
            then(it.content).isEqualTo(NotificationMessageUtils.getAskMessage(notificationEvent.cookieTitle))
        }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun handleNotificationEventForTransaction() {
        // given
        val senderUser = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .sample()
        val savedSenderUser = userRepository.save(senderUser)

        val receiverUser = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .setNotNull("deviceToken")
            .sample()
        val savedReceiverUser = userRepository.save(receiverUser)

        val notificationEvent = fixture.giveMeBuilder(TransactionNotificationEvent::class.java)
            .set("senderId", savedSenderUser.id)
            .set("receiverId", savedReceiverUser.id)
            .sample()

        // when
        eventPublisher.publishEvent(notificationEvent)

        val notifications = notificationRepository.findAll()

        // then
        then(notifications).hasSize(1)
        notifications.forEach { it ->
            then(it.type).isEqualTo(NotificationType.Transaction)
            then(it.title).isEqualTo("??????")
            then(it.receiverUserId).isEqualTo(notificationEvent.receiverId)
            then(it.senderUserId).isEqualTo(notificationEvent.senderId)
            then(it.createdAt).isEqualTo(notificationEvent.createdAt)
            then(it.cookieId).isEqualTo(notificationEvent.cookieId)
            then(it.content).isEqualTo(
                NotificationMessageUtils.getTransactionMessage(
                    savedSenderUser.nickname,
                    notificationEvent.cookieTitle,
                    notificationEvent.hammerCount
                )
            )
        }
    }

    @AfterEach
    internal fun tearDown() {
        userRepository.deleteAll()
        viewCountRepository.deleteAll()
        notificationRepository.deleteAll()
    }
}
