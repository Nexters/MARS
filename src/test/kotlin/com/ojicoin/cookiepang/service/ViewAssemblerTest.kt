package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
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
            .set("authorUserId", authorUserId)
            .set("ownedUserId", ownedUserId)
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!

        // when
        val actual = sut.cookieView(viewUserId = ownedUserId, cookieId = cookieId)

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
    fun cookieViewHidden() {
        val viewUserId = -1L
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
            .set("authorUserId", authorUserId)
            .set("ownedUserId", ownedUserId)
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!

        // when
        val actual = sut.cookieView(viewUserId = viewUserId, cookieId = cookieId)

        then(actual.answer).isNull()
    }

    @RepeatedTest(REPEAT_COUNT)
    fun timelineView() {
        val userId = 1L
        val owner = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val ownedUserId = userRepository.save(owner).id!!

        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("ownedUserId", ownedUserId)
            .sample()
        cookieRepository.save(cookie)
        val answer = if (cookie.status == ACTIVE) {
            cookie.content
        } else {
            null
        }

        // when
        val actual = sut.timelineView(userId = userId).feeds[0]

        then(actual.userNickname).isEqualTo(owner.nickname)
        then(actual.answer).isEqualTo(answer)
        then(actual.question).isEqualTo(cookie.title)
        then(actual.price).isEqualTo(cookie.price)
    }

    @AfterEach
    internal fun tearDown() {
        cookieRepository.deleteAll()
    }
}
