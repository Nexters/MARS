package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.repository.ViewCountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ViewCountService(@Autowired val viewCountRepository: ViewCountRepository) {
    fun getAllViewCountsByCookieId(cookieId: Long): Long =
        viewCountRepository.findAllByCookieId(cookieId).sumOf { it.count }
}
