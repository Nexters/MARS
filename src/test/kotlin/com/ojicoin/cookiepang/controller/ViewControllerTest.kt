package com.ojicoin.cookiepang.controller

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ViewControllerTest(
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val userRepository: UserRepository,
) : SpringContextFixture() {
    @Test
    fun cookieDetailView() {
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
        val cookieId = cookieRepository.save(cookie).id
        val userId = -1

        Given {
            this
        } When {
            get("/users/$userId/cookies/$cookieId/detail")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun timelineView() {
        val userId = 1L

        Given {
            this
        } When {
            get("/users/$userId")
        } Then {
            statusCode(200)
        }
    }
}
