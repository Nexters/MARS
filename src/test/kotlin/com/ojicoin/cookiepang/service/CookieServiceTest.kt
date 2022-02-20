package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.config.CacheTemplate
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieHistory
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.CookieStatus.HIDDEN
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.repository.CookieHistoryRepository
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import java.math.BigInteger

class CookieServiceTest(
    @Autowired val sut: CookieService,
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val cookieHistoryRepository: CookieHistoryRepository,
    @Autowired val userRepository: UserRepository,
    @Qualifier("transferInfoByTxHashCacheTemplate") val cacheTemplate: CacheTemplate<TransferEventLog>,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        val createCookie = fixture.giveMeOne(CreateCookie::class.java)
        val transferInfo = fixture.giveMeOne(TransferEventLog::class.java)
        cacheTemplate[createCookie.txHash] = transferInfo

        val created = sut.create(createCookie)

        then(created.title).isEqualTo(createCookie.question)
        then(created.open(createCookie.ownedUserId)).isEqualTo(createCookie.answer)
        then(created.price).isEqualTo(createCookie.price)
        then(created.ownedUserId).isEqualTo(createCookie.ownedUserId)
        then(created.authorUserId).isEqualTo(createCookie.authorUserId)
        then(created.txHash).isEqualTo(createCookie.txHash)
        then(created.nftTokenId).isEqualTo(transferInfo.nftTokenId)
        then(created.fromBlockAddress).isEqualTo(transferInfo.blockNumber)
        then(created.categoryId).isEqualTo(createCookie.categoryId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun createDuplicateTokenAddressThrows() {
        val createCookie = fixture.giveMeOne(CreateCookie::class.java)
        val transferInfo = fixture.giveMeOne(TransferEventLog::class.java)
        cacheTemplate[createCookie.txHash] = transferInfo
        val createCookieWithSameTokenAddress = fixture.giveMeBuilder(CreateCookie::class.java)
            .set("txHash", createCookie.txHash)
            .sample()
        sut.create(createCookie)

        thenThrownBy { sut.create(createCookieWithSameTokenAddress) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Attempting duplicate token creation")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun update() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .sample()
        val saved = cookieRepository.save(cookie)
        val updateCookie = fixture.giveMeBuilder(UpdateCookie::class.java)
            .set(
                "status",
                Arbitraries.of(CookieStatus::class.java)
                    .filter { it != CookieStatus.DELETED }.injectNull(0.1)
            )
            .sample()

        val updated = sut.modify(cookieId = saved.id!!, updateCookie = updateCookie)

        updateCookie.price?.also { then(updated.price).isEqualTo(it) }
        updateCookie.status?.also { then(updated.status).isEqualTo(it) }
        updateCookie.purchaserUserId?.also { then(updated.ownedUserId).isEqualTo(it) }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun updateDeletedThrows() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .sample()
        val saved = cookieRepository.save(cookie)
        val updateCookie = fixture.giveMeBuilder(UpdateCookie::class.java)
            .set("status", CookieStatus.DELETED)
            .sample()

        // when, then
        thenThrownBy { sut.modify(cookieId = saved.id!!, updateCookie = updateCookie) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("cannot update cookie status to DELETED, use delete instead")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun deleteNotExistsThrows() {
        val toDeleteCookieId = -1L

        // when
        thenThrownBy { sut.delete(cookieId = toDeleteCookieId) }
            .isExactlyInstanceOf(NoSuchElementException::class.java)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun delete() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACTIVE, HIDDEN))
            .sample()
        val savedCookieId = cookieRepository.save(cookie).id!!

        val deleted = sut.delete(cookieId = savedCookieId)

        then(deleted).isNotNull
    }

    @RepeatedTest(REPEAT_COUNT)
    fun deleteDeletedCookieThrows() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACTIVE, HIDDEN))
            .sample()
        val savedCookieId = cookieRepository.save(cookie).id!!
        sut.delete(cookieId = savedCookieId)

        thenThrownBy { sut.delete(cookieId = savedCookieId) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("already deleted.")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun getOwnedCookiesSameViewUserId() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACTIVE, HIDDEN))
            .sample()

        val savedCookie = cookieRepository.save(cookie)

        val getCookiesResponse = sut.getOwnedCookies(savedCookie.ownedUserId, savedCookie.ownedUserId, 0, 100)
        then(getCookiesResponse.totalCount).isEqualTo(1)

        val foundCookie = getCookiesResponse.cookies[0]
        then(savedCookie.title).isEqualTo(foundCookie.title)
        then(savedCookie.price).isEqualTo(foundCookie.price)
        then(savedCookie.ownedUserId).isEqualTo(foundCookie.ownedUserId)
        then(savedCookie.authorUserId).isEqualTo(foundCookie.authorUserId)
        then(savedCookie.categoryId).isEqualTo(foundCookie.categoryId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun getOwnedCookiesNotSameViewUserId() {
        val activeCookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", ACTIVE)
            .sample()
        val hiddenCookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", HIDDEN)
            .sample()

        cookieRepository.save(hiddenCookie)

        val savedCookie = cookieRepository.save(activeCookie)

        val getCookiesResponse = sut.getOwnedCookies(savedCookie.ownedUserId, savedCookie.authorUserId, 0, 100)
        then(getCookiesResponse.totalCount).isEqualTo(1)

        val foundCookie = getCookiesResponse.cookies[0]
        then(savedCookie.title).isEqualTo(foundCookie.title)
        then(savedCookie.price).isEqualTo(foundCookie.price)
        then(savedCookie.ownedUserId).isEqualTo(foundCookie.ownedUserId)
        then(savedCookie.authorUserId).isEqualTo(foundCookie.authorUserId)
        then(savedCookie.categoryId).isEqualTo(foundCookie.categoryId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun getAuthorCookiesSameViewUserId() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACTIVE, HIDDEN))
            .sample()

        val savedCookie = cookieRepository.save(cookie)

        val getCookiesResponse = sut.getAuthorCookies(savedCookie.authorUserId, savedCookie.authorUserId, 0, 100)
        then(getCookiesResponse.totalCount).isEqualTo(1)

        val foundCookie = getCookiesResponse.cookies[0]
        then(savedCookie.title).isEqualTo(foundCookie.title)
        then(savedCookie.price).isEqualTo(foundCookie.price)
        then(savedCookie.ownedUserId).isEqualTo(foundCookie.ownedUserId)
        then(savedCookie.authorUserId).isEqualTo(foundCookie.authorUserId)
        then(savedCookie.categoryId).isEqualTo(foundCookie.categoryId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun getAuthorCookiesNotSameViewUserId() {
        val activeCookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", ACTIVE)
            .sample()
        val hiddenCookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", HIDDEN)
            .sample()

        cookieRepository.save(hiddenCookie)

        val savedCookie = cookieRepository.save(activeCookie)

        val getCookiesResponse = sut.getAuthorCookies(savedCookie.authorUserId, savedCookie.ownedUserId, 0, 100)
        then(getCookiesResponse.totalCount).isEqualTo(1)

        val foundCookie = getCookiesResponse.cookies[0]
        then(savedCookie.title).isEqualTo(foundCookie.title)
        then(savedCookie.price).isEqualTo(foundCookie.price)
        then(savedCookie.ownedUserId).isEqualTo(foundCookie.ownedUserId)
        then(savedCookie.authorUserId).isEqualTo(foundCookie.authorUserId)
        then(savedCookie.categoryId).isEqualTo(foundCookie.categoryId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun findCookieHistories() {
        val blockNumber = BigInteger.ONE // for not request to blockchain
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("status", ACTIVE)
            .set("fromBlockAddress", blockNumber)
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!
        val creator = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val creatorId = userRepository.save(creator).id!!
        val cookieHistory = fixture.giveMeBuilder<CookieHistory>()
            .setNull("id")
            .set("cookieId", cookieId)
            .set("creatorId", creatorId)
            .set("blockNumber", blockNumber)
            .sample()
        cookieHistoryRepository.save(cookieHistory)

        val actual = sut.findCookieHistories(cookieId)

        then(actual).hasSize(1)
        with(actual[0]) {
            then(action).isEqualTo(cookieHistory.action)
            then(cookieId).isEqualTo(cookieHistory.cookieId)
            then(creatorName).isEqualTo(cookieHistory.creatorName)
            then(title).isEqualTo(cookieHistory.title)
            then(hammerPrice).isEqualTo(cookieHistory.hammerPrice)
            then(nftTokenId).isEqualTo(cookieHistory.nftTokenId)
            then(blockNumber).isEqualTo(cookieHistory.blockNumber)
            then(createdAt).isEqualTo(cookieHistory.createdAt)
        }
    }

    @AfterEach
    internal fun tearDown() {
        cookieRepository.deleteAll()
        cookieHistoryRepository.deleteAll()
    }
}
