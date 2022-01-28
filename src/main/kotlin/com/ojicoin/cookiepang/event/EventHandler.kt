package com.ojicoin.cookiepang.event

import com.ojicoin.cookiepang.domain.ViewCount
import com.ojicoin.cookiepang.repository.ViewCountRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class EventHandler(private val viewCountRepository: ViewCountRepository) {
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
}
