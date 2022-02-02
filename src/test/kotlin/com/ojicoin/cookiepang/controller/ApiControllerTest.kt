package com.ojicoin.cookiepang.controller

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.repository.CookieRepository
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ApiControllerTest : SpringContextFixture() {
    @Test
    fun viewCookie(@Autowired cookieRepository: CookieRepository) {
        val userId = 1L
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("ownedUserId", userId)
            .sample()
        val cookieId = cookieRepository.save(cookie).id

        Given {
            this
        } When {
            get("/users/$userId/cookies/$cookieId")
        } Then {
            statusCode(200)
        }
    }
}
