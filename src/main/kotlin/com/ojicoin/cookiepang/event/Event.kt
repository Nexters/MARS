package com.ojicoin.cookiepang.event

import org.springframework.context.ApplicationEvent

class Event {
    // ktlint 에러 해결용
}

data class ViewCookieEvent(
    private val source: Any,
    val userId: Long,
    val cookieId: Long,
) : ApplicationEvent(source) {
    override fun getSource(): Any {
        return super.getSource()
    }
}
