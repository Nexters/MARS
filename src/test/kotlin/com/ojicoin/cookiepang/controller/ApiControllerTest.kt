package com.ojicoin.cookiepang.controller

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.config.CacheTemplate
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class ApiControllerTest(
    @Autowired val userRepository: UserRepository,
    @Autowired val cookieRepository: CookieRepository,
    @Qualifier("transferInfoByTxHashCacheTemplate") val cacheTemplate: CacheTemplate<TransferEventLog>,
) : SpringContextFixture() {
    @Test
    fun createCookie() {
        val createCookie = fixture.giveMeOne<CreateCookie>()
        val transferInfo = fixture.giveMeOne(TransferEventLog::class.java)
        cacheTemplate[createCookie.txHash] = transferInfo

        Given {
            body(createCookie)
            header("content-type", "application/json")
        } When {
            post("/cookies")
        } Then {
            statusCode(201)
        }
    }

    @Test
    fun viewCategories() {
        Given {
            header("content-type", "application/json")
        } When {
            get("/categories")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun getCookieDetailView() {
        val userBuilder = fixture.giveMeBuilder<User>()
            .setNull("id")
        val authorUserId = userRepository.save(userBuilder.sample()).id!!
        val ownerUserId = userRepository.save(userBuilder.sample()).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("status", CookieStatus.HIDDEN)
            .set("authorUserId", authorUserId)
            .set("ownedUserId", ownerUserId)
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!

        Given {
            header("content-type", "application/json")
        } When {
            get("/users/$authorUserId/cookies/$cookieId/detail")
        } Then {
            statusCode(403)
        }
    }

    @AfterEach
    internal fun tearDown() {
        userRepository.deleteAll()
        cookieRepository.deleteAll()
    }
}
