package com.ojicoin.cookiepang

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KFixtureMonkey
import com.ojicoin.cookiepang.repository.ViewCountRepository
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var viewCountRepository: ViewCountRepository

    @BeforeEach
    internal fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    internal fun clear() {
        viewCountRepository.deleteAll() // TODO: 이벤트 초기화 처리로 변경
    }
}
