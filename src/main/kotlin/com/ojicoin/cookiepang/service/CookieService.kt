package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.repository.CookieRepository
import org.springframework.stereotype.Service

@Service
class CookieService(
    private val cookieRepository: CookieRepository,
) {
    fun get(cookieId: Long): Cookie = cookieRepository.findById(cookieId).orElseThrow()
}
