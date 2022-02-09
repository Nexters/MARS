package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.CookieStatus.HIDDEN
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.repository.CookieRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class CookieServiceTest(
    @Autowired val sut: CookieService,
    @Autowired val cookieRepository: CookieRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        val createCookie = fixture.giveMeOne(CreateCookie::class.java)

        val created = sut.create(createCookie)

        then(created.title).isEqualTo(createCookie.question)
        then(created.content).isEqualTo(createCookie.answer)
        then(created.price).isEqualTo(createCookie.price)
        then(created.ownedUserId).isEqualTo(createCookie.ownedUserId)
        then(created.authorUserId).isEqualTo(createCookie.authorUserId)
        then(created.tokenAddress).isEqualTo(createCookie.tokenAddress)
        then(created.cookieCategoryId).isEqualTo(createCookie.categoryId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun createDuplicateTokenAddressThrows() {
        val createCookie = fixture.giveMeOne(CreateCookie::class.java)
        val createCookieWithSameTokenAddress = fixture.giveMeBuilder(CreateCookie::class.java)
            .set("tokenAddress", createCookie.tokenAddress)
            .sample()
        sut.create(createCookie)

        thenThrownBy { sut.create(createCookieWithSameTokenAddress) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Attempting duplicate token creation")
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

    @AfterEach
    internal fun tearDown() {
        cookieRepository.deleteAll()
    }
}
