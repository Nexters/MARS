package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.SpringContextFixture
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class ViewControllerTest : SpringContextFixture() {
    @Test
    fun cookieDetailView() {
        val userId = 1L
        val cookieId = 2L

        Given {
            this
        } When {
            get("/users/$userId/cookies/$cookieId/detail")
        } Then {
            statusCode(200)
        }
    }
}
