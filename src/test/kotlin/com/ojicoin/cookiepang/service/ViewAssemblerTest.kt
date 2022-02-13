package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.CookieStatus.DELETED
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

class ViewAssemblerTest(
    @Autowired val sut: ViewAssembler,
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val userRepository: UserRepository,
    @Value("\${contract.address}") val contractAddress: String,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun cookieView() {
        val creator = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val collector = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val authorUserId = userRepository.save(creator).id!!
        val ownedUserId = userRepository.save(collector).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("status", Arbitraries.of(CookieStatus::class.java).filter { it != DELETED })
            .set("authorUserId", authorUserId)
            .set("ownedUserId", ownedUserId)
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!

        // when
        val actual = sut.cookieView(viewerId = ownedUserId, cookieId = cookieId)

        then(actual.question).isEqualTo(cookie.title)
        then(actual.answer).isEqualTo(cookie.content)
        then(actual.collectorName).isEqualTo(collector.nickname)
        then(actual.creatorName).isEqualTo(creator.nickname)
        then(actual.contractAddress).isEqualTo(contractAddress)
        then(actual.price).isEqualTo(cookie.price)
        then(actual.tokenAddress).isEqualTo(cookie.tokenAddress)
        then(actual.viewCount).isEqualTo(0L)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun cookieViewNotOwner() {
        val creator = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val collector = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val viewer = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val authorUserId = userRepository.save(creator).id!!
        val ownedUserId = userRepository.save(collector).id!!
        val viewerId = userRepository.save(viewer).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("authorUserId", authorUserId)
            .set("ownedUserId", ownedUserId)
            .set("status", CookieStatus.ACTIVE)
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!

        // when
        val actual = sut.cookieView(viewerId = viewerId, cookieId = cookieId)

        then(actual.answer).isNull()
    }

    @AfterEach
    internal fun tearDown() {
        cookieRepository.deleteAll()
    }
}
