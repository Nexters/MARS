package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.repository.CookieRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class CookieService(
    private val cookieRepository: CookieRepository,
) {
    fun get(cookieId: Long): Cookie = cookieRepository.findById(cookieId).orElseThrow()

    fun create(dto: CreateCookie): Cookie {
        if (cookieRepository.findByTokenAddress(dto.tokenAddress) != null) {
            throw IllegalArgumentException("Attempting duplicate token creation.")
        }

        return cookieRepository.save(
            Cookie(
                title = dto.question,
                content = dto.answer,
                price = dto.price,
                authorUserId = dto.authorUserId,
                ownedUserId = dto.ownedUserId,
                tokenAddress = dto.tokenAddress,
                cookieTagId = dto.categoryId,
                imageUrl = null,
                status = ACTIVE,
                createdAt = Instant.now(),
            )
        )
    }
}
