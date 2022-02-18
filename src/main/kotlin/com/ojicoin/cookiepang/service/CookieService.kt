package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.config.CacheTemplate
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.CookieStatus.DELETED
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.TransferInfo
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
    @Qualifier("transferInfoByTxHashCacheTemplate") private val transferInfoByTxHashCacheTemplate: CacheTemplate<TransferInfo>,
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
            ?: cookieContractService.getTransferInfoByTxHash(dto.txHash)

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
}
