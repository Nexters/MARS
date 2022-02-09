package com.ojicoin.cookiepang.controller

import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.dto.CreateCookie
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class ApiControllerTest : SpringContextFixture() {
    @Test
    fun createCookie() {
        val createCookie = fixture.giveMeOne<CreateCookie>()

        Given {
            body(createCookie)
            header("content-type", "application/json")
        } When {
            post("/cookies")
        } Then {
            statusCode(201)
        }
    }
}
