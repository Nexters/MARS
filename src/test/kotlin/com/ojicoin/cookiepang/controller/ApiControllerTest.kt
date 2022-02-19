package com.ojicoin.cookiepang.controller

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.config.CacheTemplate
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import com.ojicoin.cookiepang.dto.CreateCookie
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Qualifier

class ApiControllerTest(
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
}
