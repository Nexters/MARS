package com.ojicoin.cookiepang

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.customizer.ArbitraryCustomizer
import com.navercorp.fixturemonkey.generator.FieldArbitraries
import com.navercorp.fixturemonkey.kotlin.KFixtureMonkeyBuilder
import com.ojicoin.cookiepang.contract.service.CookieContractService
import com.ojicoin.cookiepang.event.ViewCookieEvent
import com.ojicoin.cookiepang.repository.ViewCountRepository
import io.restassured.RestAssured
import net.jqwik.api.Arbitraries
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationEvent

const val REPEAT_COUNT = 5

val fixture: FixtureMonkey = KFixtureMonkeyBuilder()
    .addCustomizer(ViewCookieEvent::class.java, ApplicationEventCustomizer())
    .build()

@SpringBootTest(classes = [CookiepangApplication::class], webEnvironment = RANDOM_PORT)
abstract class SpringContextFixture {
    @LocalServerPort
    protected var port = 0

    protected val fixture: FixtureMonkey = com.ojicoin.cookiepang.fixture

    @MockBean
    lateinit var cookieContractService: CookieContractService

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

class ApplicationEventCustomizer<T : ApplicationEvent> : ArbitraryCustomizer<T> {
    override fun customizeFields(type: Class<T>?, fieldArbitraries: FieldArbitraries) {
        fieldArbitraries.putArbitrary("source", Arbitraries.of(EmptyObject()))
    }

    override fun customizeFixture(`object`: T?): T? = `object`
}

class EmptyObject
