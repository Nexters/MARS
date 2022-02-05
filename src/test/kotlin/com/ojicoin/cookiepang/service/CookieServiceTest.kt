package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class CookieServiceTest(
    @Autowired val sut: CookieService,
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val userRepository: UserRepository,
) : SpringContextFixture() {
    @RepeatedTest(REPEAT_COUNT)
    fun view() {
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
            .set("status", CookieStatus.ACTIVE)
            .sample()
        val actual = cookieRepository.save(cookie)

        val expected = sut.view(userId = cookie.ownedUserId, cookieId = actual.id!!)

        then(actual).isEqualTo(expected)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun viewNotOwnedCookie() {
        // given
        val notOwnedUserId = 2L
        val ownedUserId = 1L
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("ownedUserId", ownedUserId)
            .sample()
        val expected = cookieRepository.save(cookie)

        // when, then
        thenThrownBy { sut.view(userId = notOwnedUserId, cookieId = expected.id!!) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
    }
}
