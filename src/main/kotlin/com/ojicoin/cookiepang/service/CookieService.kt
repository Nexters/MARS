package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.event.ViewCookieEvent
import com.ojicoin.cookiepang.repository.CookieRepository
import org.springframework.stereotype.Service

@Service
class CookieService(
    private val cookieRepository: CookieRepository,
) {
    fun view(userId: Long, cookieId: Long): Cookie {
        val cookie: Cookie = cookieRepository.findById(cookieId).orElseThrow()
        if (cookie.ownedUserId != userId) {
            throw IllegalArgumentException("$userId access other users cookie ${cookie.id}")
        }

        cookie.addEvent(ViewCookieEvent(source = cookie, userId = userId, cookieId = cookieId))
        cookieRepository.save(cookie)
        return cookie
    }
}
