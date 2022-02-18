package com.ojicoin.cookiepang.event

import org.springframework.context.ApplicationEvent

// For prevent ktlint error
class Event

data class ViewCookieEvent(
    private val source: Any,
    val userId: Long,
    val cookieId: Long,
) : ApplicationEvent(source) {
    override fun getSource(): Any {
        return super.getSource()
    }
}
