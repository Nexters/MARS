package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.config.CacheTemplate
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieHistory
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.CookieStatus.DELETED
import com.ojicoin.cookiepang.domain.CookieStatus.HIDDEN
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.CreateDefaultCookie
import com.ojicoin.cookiepang.dto.CreateDefaultCookies
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.exception.InvalidDomainStatusException
import com.ojicoin.cookiepang.exception.InvalidRequestException
import com.ojicoin.cookiepang.repository.CategoryRepository
import com.ojicoin.cookiepang.repository.CookieHistoryRepository
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.NotificationRepository
import com.ojicoin.cookiepang.repository.UserRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Pageable
import java.math.BigInteger

class CookieServiceTest(
    @Autowired val sut: CookieService,
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val cookieHistoryRepository: CookieHistoryRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val categoryRepository: CategoryRepository,
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
        then(created.nftTokenId).isEqualTo(transferInfo.nftTokenId)
        then(created.fromBlockAddress).isEqualTo(transferInfo.blockNumber)
        then(created.categoryId).isEqualTo(createCookie.categoryId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun update() {
        val sendUser = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .sample()
        val savedSendUser = userRepository.save(sendUser)
        val receiveUser = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .sample()
        val savedReceiveUser = userRepository.save(receiveUser)

        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(CookieStatus::class.java).filter { it != DELETED })
            .set("ownedUserId", savedSendUser.id)
            .sample()
        val saved = cookieRepository.save(cookie)
        val updateCookie = fixture.giveMeBuilder(UpdateCookie::class.java)
            .set(
                "status",
                Arbitraries.of(CookieStatus::class.java)
                    .filter { it != DELETED }.injectNull(0.1)
            )
            .set("purchaserUserId", savedReceiveUser.id)
            .sample()

        val updated = sut.modify(cookieId = saved.id!!, updateCookie = updateCookie)

        updateCookie.price?.also { then(updated.price).isEqualTo(it) }
        updateCookie.status?.also { then(updated.status).isEqualTo(it) }
        updateCookie.purchaserUserId?.also { then(updated.ownedUserId).isEqualTo(it) }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun updateDeletedCookieThrows() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", DELETED)
            .sample()
        val saved = cookieRepository.save(cookie)
        val updateCookie = fixture.giveMeBuilder(UpdateCookie::class.java)
            .set("status", Arbitraries.of(CookieStatus::class.java).filter { it != DELETED })
            .sample()

        // when, then
        thenThrownBy { sut.modify(cookieId = saved.id!!, updateCookie = updateCookie) }
            .isExactlyInstanceOf(NoSuchElementException::class.java)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun updateCookieToDeletedThrows() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(CookieStatus::class.java).filter { it != DELETED })
            .sample()
        val saved = cookieRepository.save(cookie)
        val updateCookie = fixture.giveMeBuilder(UpdateCookie::class.java)
            .set("status", DELETED)
            .sample()

        // when, then
        thenThrownBy { sut.modify(cookieId = saved.id!!, updateCookie = updateCookie) }
            .isExactlyInstanceOf(InvalidRequestException::class.java)
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
            .isExactlyInstanceOf(InvalidDomainStatusException::class.java)
            .hasMessageContaining("already deleted.")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun getOwnedCookies() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACTIVE, HIDDEN))
            .sample()
        val savedCookie = cookieRepository.save(cookie)

        val actual = sut.getAllOwnedCookies(savedCookie.ownedUserId, pageable = Pageable.unpaged())[0]

        then(savedCookie.ownedUserId).isEqualTo(actual.ownedUserId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun getAuthorCookies() {
        val cookie = fixture.giveMeBuilder(Cookie::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACTIVE, HIDDEN))
            .sample()

        val savedCookie = cookieRepository.save(cookie)

        val actual = sut.getAllAuthorCookies(savedCookie.authorUserId, pageable = Pageable.unpaged())[0]

        then(savedCookie.authorUserId).isEqualTo(actual.authorUserId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun findCookieHistories() {
        val blockNumber = BigInteger.ONE // for not request to blockchain
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("status", ACTIVE)
            .set("fromBlockAddress", BigInteger.valueOf(1))
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

    @RepeatedTest(REPEAT_COUNT)
    fun createDefaultCookies() {
        // given
        val user = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .set("finishOnboard", false)
            .sample()
        userRepository.save(user)

        val category = fixture.giveMeBuilder(Category::class.java)
            .setNull("id")
            .set("name", "자유")
            .sample()
        categoryRepository.save(category)

        val createDefaultCookie = fixture.giveMeOne(CreateDefaultCookie::class.java)
        val createDefaultCookies = fixture.giveMeBuilder(CreateDefaultCookies::class.java)
            .set("creatorId", user.id!!)
            .set("defaultCookies", listOf(createDefaultCookie))
            .sample()

        val transferEventLog = fixture.giveMeBuilder(TransferEventLog::class.java)
            .setNotNull("nftTokenId")
            .setNotNull("blockNumber")
            .sample()

        given(cookieContractService.createDefaultCookie(any())).willReturn(transferEventLog)

        // when
        val cookieList = sut.createDefaultCookies(createDefaultCookies)

        // then
        println("$cookieList")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun createDefaultCookiesFinishedOnboardUser() {
        val user = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .set("finishOnboard", true)
            .sample()

        userRepository.save(user)

        thenThrownBy { sut.createDefaultCookies(CreateDefaultCookies(user.id!!, listOf())) }
            .isExactlyInstanceOf(InvalidRequestException::class.java)
            .hasMessageContaining("Already onboard finished user.")
    }

    @AfterEach
    internal fun tearDown() {
        userRepository.deleteAll()
        cookieRepository.deleteAll()
        cookieHistoryRepository.deleteAll()
        notificationRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    fun <T> any(): T = Mockito.any()
}
