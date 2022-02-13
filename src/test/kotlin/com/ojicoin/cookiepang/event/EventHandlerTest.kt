package com.ojicoin.cookiepang.event

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.repository.NotificationRepository
import com.ojicoin.cookiepang.repository.ViewCountRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher

class EventHandlerTest(
    @Autowired val eventPublisher: ApplicationEventPublisher,
    @Autowired val viewCountRepository: ViewCountRepository,
    @Autowired val notificationRepository: NotificationRepository,
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
    fun handleNotificationEvent() {
        // given
        val notificationEvent = fixture.giveMeBuilder(AskNotificationEvent::class.java)
            .sample()

        // when
        eventPublisher.publishEvent(notificationEvent)

        // then
        then(notificationRepository.findAll()).hasSize(1)
    }

    @AfterEach
    internal fun tearDown() {
        viewCountRepository.deleteAll()
        notificationRepository.deleteAll()
    }
}
