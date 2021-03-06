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
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.event.TransactionNotificationEvent
import com.ojicoin.cookiepang.exception.InvalidDomainStatusException
import com.ojicoin.cookiepang.exception.InvalidRequestException
import com.ojicoin.cookiepang.repository.CookieHistoryRepository
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.web3j.protocol.core.DefaultBlockParameter
import java.math.BigInteger
import java.time.Instant

@Service
class CookieService(
    private val cookieRepository: CookieRepository,
    private val cookieContractService: CookieContractService,
    private val cookieHistoryRepository: CookieHistoryRepository,
    private val userRepository: UserRepository, // TODO: 구조 리팩토링
    private val eventPublisher: ApplicationEventPublisher,
    @Qualifier("transferInfoByTxHashCacheTemplate") private val transferInfoByTxHashCacheTemplate: CacheTemplate<TransferEventLog>,
) {
    private val TRANSACTION_HEX_PREFIX_DIGIT_LENGTH = 2

    fun get(cookieId: Long): Cookie = cookieRepository.findCookieById(cookieId)!!

    fun getActiveCookies(pageable: Pageable): List<Cookie> =
        cookieRepository.findByStatusIs(pageable = pageable)

    fun countActiveCookies(): Long = cookieRepository.countByStatusIs()

    fun getActiveCookiesByCategoryId(categoryId: Long, pageable: Pageable): List<Cookie> =
        cookieRepository.findByStatusIsAndCategoryId(
            categoryId = categoryId,
            pageable = pageable
        )

    fun countActiveCookiesByCategoryId(categoryId: Long): Long =
        cookieRepository.countByStatusIsAndCategoryId(categoryId = categoryId)

    fun getAllOwnedCookies(ownedUserId: Long, pageable: Pageable): List<Cookie> =
        cookieRepository.findByStatusIsNotAndOwnedUserId(ownedUserId = ownedUserId, pageable = pageable)

    fun countAllOwnedCookies(ownedUserId: Long): Long =
        cookieRepository.countByStatusIsNotAndOwnedUserId(ownedUserId = ownedUserId)

    fun getAllAuthorCookies(authorUserId: Long, pageable: Pageable): List<Cookie> =
        cookieRepository.findByStatusIsNotAndAuthorUserId(authorUserId = authorUserId, pageable = pageable)

    fun countAllAuthorCookies(authorUserId: Long): Long =
        cookieRepository.countByStatusIsNotAndAuthorUserId(authorUserId = authorUserId)

    fun create(dto: CreateCookie): Cookie {
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
        val cookie = cookieRepository.findCookieById(cookieId)!!
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

    fun getUserAddress(cookie: Cookie): String {
        val adminTransferEventLog = cookieContractService.getCookieTransferLogByNftTokenId(
            fromBlock = DefaultBlockParameter.valueOf(cookie.fromBlockAddress),
            nftTokenId = cookie.nftTokenId
        ).findLast {
            getBigIntegerFromHexStr(it.fromAddrees) != BigInteger.ZERO
        }
        return adminTransferEventLog!!.toAddress
    }

    @Transactional
    fun modify(cookieId: Long, updateCookie: UpdateCookie): Cookie {
        val cookie = cookieRepository.findCookieById(cookieId) ?: throw NoSuchElementException()

        if (updateCookie.status == DELETED) {
            throw InvalidRequestException("cannot update cookie status to DELETED, use delete instead")
                .with("cookieId", cookie.id!!)
        }

        val blockAddress = if (updateCookie.txHash != null) {
            val cookieEvent = transferInfoByTxHashCacheTemplate[updateCookie.txHash]
                ?: cookieContractService.getCookieEventLogByTxHash(updateCookie.txHash)
            cookieEvent.blockNumber
        } else {
            null
        }

        val previousOwnedUser = cookie.ownedUserId
        val newOwnedUser = updateCookie.purchaserUserId

        cookie.apply(blockAddress = blockAddress, updateCookie = updateCookie)
        val savedCookie = cookieRepository.save(cookie)

        if (updateCookie.purchaserUserId != null) {
            eventPublisher.publishEvent(
                TransactionNotificationEvent(
                    receiverId = previousOwnedUser,
                    senderId = newOwnedUser!!,
                    cookieId = cookie.id!!,
                    cookieTitle = cookie.title,
                    hammerCount = cookie.price
                )
            )
        }

        return savedCookie
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

    private fun getBigIntegerFromHexStr(cookieIdHex: String): BigInteger {
        return BigInteger(cookieIdHex.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH), 16)
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
