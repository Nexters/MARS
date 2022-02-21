package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.config.CacheTemplate
import com.ojicoin.cookiepang.contract.event.CookieEventLog
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import com.ojicoin.cookiepang.contract.service.CookieContractService
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieHistory
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.CookieStatus.DELETED
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.GetCookiesResponse
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.exception.DuplicateDomainException
import com.ojicoin.cookiepang.exception.InvalidDomainStatusException
import com.ojicoin.cookiepang.exception.InvalidRequestException
import com.ojicoin.cookiepang.repository.CookieHistoryRepository
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.web3j.protocol.core.DefaultBlockParameter
import java.time.Instant

@Service
class CookieService(
    private val cookieRepository: CookieRepository,
    private val cookieContractService: CookieContractService,
    private val cookieHistoryRepository: CookieHistoryRepository,
    private val userRepository: UserRepository, // TODO: 구조 리팩토링
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
            throw DuplicateDomainException(domainType = "Cookie", message = "Attempting duplicate token creation.")
                .with("txHash", dto.txHash)
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
    fun findCookieHistories(cookieId: Long): List<CookieHistory> {
        val cookie = cookieRepository.findActiveCookieById(cookieId)!!
        val cookieHistories = cookieHistoryRepository.findByCookieId(cookieId)
        if (cookieHistories.isNotEmpty() && cookie.fromBlockAddress == cookieHistories.maxOf { it.blockNumber }) {
            return cookieHistories
        }

        val newCookieHistories = cookieContractService.getCookieEventLogByNftTokenId(
            fromBlock = DefaultBlockParameter.valueOf(cookie.fromBlockAddress),
            nftTokenId = cookie.nftTokenId
        ).map {
            val creator = userRepository.findByWalletAddress(it.fromAddress)
                ?: throw InvalidRequestException("User with given wallet address is not registered.")
                    .with("walletAddress", it.fromAddress)
            it.toCookieHistory(cookie, creator)
        }
        if (newCookieHistories.isNotEmpty()) {
            cookie.fromBlockAddress = newCookieHistories.last().blockNumber
            cookieRepository.save(cookie)
            cookieHistoryRepository.saveAll(newCookieHistories)
        }

        return cookieHistories + newCookieHistories
    }

    @Transactional
    fun modify(cookieId: Long, updateCookie: UpdateCookie): Cookie {
        val cookie = cookieRepository.findById(cookieId).orElseThrow()
        if (updateCookie.status == DELETED) {
            throw InvalidRequestException("cannot update cookie status to DELETED, use delete instead")
                .with("cookieId", cookie.id!!)
        }

        cookie.apply(updateCookie)
        return cookieRepository.save(cookie)
    }

    @Transactional
    fun delete(cookieId: Long): Cookie {
        val toDeleteCookie = cookieRepository.findById(cookieId).orElseThrow()
        if (toDeleteCookie.status == DELETED) {
            throw InvalidDomainStatusException(
                domainType = "cookie",
                message = "Cookie is already deleted."
            )
                .with("cookieId", cookieId)
                .with("status", toDeleteCookie.status)
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

fun CookieEventLog.toCookieHistory(cookie: Cookie, creator: User) =
    CookieHistory(
        action = this.cookieEventStatus.toAction(),
        cookieId = cookie.id!!,
        hammerPrice = this.hammerPrice,
        nftTokenId = this.nftTokenId,
        title = cookie.title,
        creatorName = creator.nickname,
        blockNumber = this.blockNumber,
        createdAt = this.createdAt.toInstant()
    )
