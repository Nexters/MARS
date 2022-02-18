package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.config.CacheTemplate
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import com.ojicoin.cookiepang.contract.service.CookieContractService
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.CookieStatus.DELETED
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.GetCookiesResponse
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.repository.CookieRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CookieService(
    private val cookieRepository: CookieRepository,
    private val cookieContractService: CookieContractService,
    @Qualifier("transferInfoByTxHashCacheTemplate") private val transferInfoByTxHashCacheTemplate: CacheTemplate<TransferEventLog>,
) {
    fun get(cookieId: Long): Cookie = cookieRepository.findActiveCookieById(cookieId)!!

    fun getCookies(page: Int, size: Int): List<Cookie> =
        cookieRepository.findByStatusIsNot(pageable = PageRequest.of(page, size))

    fun getCookiesByCategoryId(categoryId: Long, page: Int, size: Int): List<Cookie> =
        cookieRepository.findByStatusIsNotAndCategoryId(
            categoryId = categoryId,
            pageable = PageRequest.of(page, size)
        )

    fun create(dto: CreateCookie): Cookie {
        if (cookieRepository.findByTxHash(dto.txHash) != null) {
            throw IllegalArgumentException("Attempting duplicate token creation.")
        }
        val transferInfo = transferInfoByTxHashCacheTemplate[dto.txHash]
            ?: cookieContractService.getTransferEventLogByTxHash(dto.txHash)

        return cookieRepository.save(
            Cookie(
                title = dto.question,
                content = dto.answer,
                price = dto.price,
                authorUserId = dto.authorUserId,
                ownedUserId = dto.ownedUserId,
                txHash = dto.txHash,
                categoryId = dto.categoryId,
                imageUrl = null,
                status = ACTIVE,
                nftTokenId = transferInfo.nftTokenId,
                fromBlockAddress = transferInfo.blockNumber,
                createdAt = Instant.now(),
            )
        )
    }

    fun publishEvent(cookie: Cookie) {
        cookieRepository.save(cookie) // TODO: 이벤트만 발행하도록 수정
    }

    @Transactional
    fun modify(cookieId: Long, updateCookie: UpdateCookie): Cookie {
        val cookie = cookieRepository.findById(cookieId).orElseThrow()
        if (updateCookie.status == DELETED) {
            throw IllegalArgumentException("cannot update cookie status to DELETED, use delete instead")
        }

        cookie.apply(updateCookie)
        return cookieRepository.save(cookie)
    }

    @Transactional
    fun delete(cookieId: Long): Cookie {
        val toDeleteCookie = cookieRepository.findById(cookieId).orElseThrow()
        if (toDeleteCookie.status == DELETED) {
            throw IllegalArgumentException("Cookie $cookieId is already deleted.")
        }

        toDeleteCookie.status = DELETED
        cookieRepository.save(toDeleteCookie)
        return toDeleteCookie
    }

    fun getOwnedCookies(userId: Long, viewUserId: Long, page: Int, size: Int): GetCookiesResponse {
        val cookies = if (userId == viewUserId) {
            // find all user owned cookies not delete status
            cookieRepository.findByStatusIsNotAndOwnedUserId(
                ownedUserId = userId,
                pageable = PageRequest.of(page, size)
            )
        } else {
            // find all user owned cookies only active status
            cookieRepository.findByStatusAndOwnedUserId(ownedUserId = userId, pageable = PageRequest.of(page, size))
        }

        return GetCookiesResponse(
            totalCount = cookies.size,
            page = page,
            size = size,
            cookies = cookies
        )
    }

    fun getAuthorCookies(userId: Long, viewUserId: Long, page: Int, size: Int): GetCookiesResponse {
        val cookies = if (userId == viewUserId) {
            // find all user author cookies without delete status
            cookieRepository.findByStatusIsNotAndAuthorUserId(
                authorUserId = userId,
                pageable = PageRequest.of(page, size)
            )
        } else {
            // find all user author cookies only active status
            cookieRepository.findByStatusAndAuthorUserId(authorUserId = userId, pageable = PageRequest.of(page, size))
        }

        return GetCookiesResponse(
            totalCount = cookies.size,
            page = page,
            size = size,
            cookies = cookies
        )
    }
}
