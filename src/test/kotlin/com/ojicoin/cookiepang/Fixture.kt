package com.ojicoin.cookiepang

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KFixtureMonkey
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort

const val REPEAT_COUNT = 5

val fixture: FixtureMonkey = KFixtureMonkey.create()

@SpringBootTest(classes = [CookiepangApplication::class], webEnvironment = RANDOM_PORT)
abstract class SpringContextFixture {
    @LocalServerPort
    protected var port = 0

    protected val fixture: FixtureMonkey = com.ojicoin.cookiepang.fixture

    @BeforeEach
    internal fun setUp() {
        RestAssured.port = port
    }
}
