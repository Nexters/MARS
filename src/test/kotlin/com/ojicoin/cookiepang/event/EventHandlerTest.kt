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
        val notificationEvent = fixture.giveMeBuilder(AskNotificationEvent::class.java)
            .sample()

        // when
        eventPublisher.publishEvent(notificationEvent)

        val notifications = notificationRepository.findAll()

        // then
        then(notifications).hasSize(1)
        notifications.forEach { it ->
            then(it.type).isEqualTo(NotificationType.Ask)
            then(it.title).isEqualTo("요청")
            then(it.receiverUserId).isEqualTo(notificationEvent.receiverUserId)
            then(it.senderUserId).isEqualTo(notificationEvent.senderUserId)
            then(it.createdAt).isEqualTo(notificationEvent.createdAt)
            then(it.askId).isEqualTo(notificationEvent.askId)
            then(it.content).isEqualTo(NotificationMessageUtils.getAskMessage(notificationEvent.cookieTitle))
        }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun handleNotificationEventForTransaction() {
        // given
        val user = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .sample()

        val savedSenderUser = userRepository.save(user)

        val notificationEvent = fixture.giveMeBuilder(TransactionNotificationEvent::class.java)
            .set("senderUserId", savedSenderUser.id)
            .sample()

        // when
        eventPublisher.publishEvent(notificationEvent)

        val notifications = notificationRepository.findAll()

        // then
        then(notifications).hasSize(1)
        notifications.forEach { it ->
            then(it.type).isEqualTo(NotificationType.Transaction)
            then(it.title).isEqualTo("판매")
            then(it.receiverUserId).isEqualTo(notificationEvent.receiverUserId)
            then(it.senderUserId).isEqualTo(notificationEvent.senderUserId)
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
