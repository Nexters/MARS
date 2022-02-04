package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.repository.CookieRepository
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class CookieServiceTest(
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val sut: CookieService,
) : SpringContextFixture() {
    @RepeatedTest(REPEAT_COUNT)
    fun view() {
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
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

    @AfterEach
    internal fun tearDown() {
        cookieRepository.deleteAll()
    }
}
